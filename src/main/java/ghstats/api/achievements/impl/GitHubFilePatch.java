package ghstats.api.achievements.impl;

import ghstats.api.integrations.github.api.PullRequestFile;
import org.wickedsource.diffparser.api.UnifiedDiffParser;
import org.wickedsource.diffparser.api.model.Line;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

final class GitHubFilePatch {

    private static final Pattern HUNK_HEADER = Pattern.compile(
            "^@@ -(\\d+)(?:,(\\d+))? \\+(\\d+)(?:,(\\d+))? @@.*$"
    );

    private GitHubFilePatch() {
    }

    static Optional<ParsedPatch> parse(PullRequestFile file) {
        if (file.patch() == null || file.patch().isBlank()) {
            return Optional.empty();
        }

        Optional<String> normalizedPatch = normalize(file.patch());
        if (normalizedPatch.isEmpty()) {
            return Optional.empty();
        }

        String unifiedDiff = "--- a/%s%n+++ b/%s%n%s%n%n".formatted(
                file.previousFilename() == null ? file.filename() : file.previousFilename(),
                file.filename(),
                normalizedPatch.orElseThrow()
        );

        try {
            var diffs = new UnifiedDiffParser().parse(unifiedDiff.getBytes(StandardCharsets.UTF_8));
            if (diffs.size() != 1 || diffs.getFirst().getHunks().isEmpty()) {
                return Optional.empty();
            }

            List<ParsedHunk> parsedHunks = new ArrayList<>();
            int additions = 0;
            int deletions = 0;
            for (var hunk : diffs.getFirst().getHunks()) {
                List<String> before = new ArrayList<>();
                List<String> after = new ArrayList<>();
                List<String> addedLines = new ArrayList<>();
                int hunkAdditions = 0;
                int hunkDeletions = 0;
                int neutralLines = 0;

                for (Line line : hunk.getLines()) {
                    switch (line.getLineType()) {
                        case FROM -> {
                            before.add(decode(line.getContent()));
                            hunkDeletions++;
                        }
                        case TO -> {
                            String content = decode(line.getContent());
                            after.add(content);
                            addedLines.add(content);
                            hunkAdditions++;
                        }
                        case NEUTRAL -> {
                            if (!line.getContent().startsWith("=")) {
                                return Optional.empty();
                            }
                            String content = decode(line.getContent().substring(1));
                            before.add(content);
                            after.add(content);
                            neutralLines++;
                        }
                    }
                }

                if (hunkDeletions + neutralLines != hunk.getFromFileRange().getLineEnd()
                        || hunkAdditions + neutralLines != hunk.getToFileRange().getLineEnd()) {
                    return Optional.empty();
                }

                additions += hunkAdditions;
                deletions += hunkDeletions;
                parsedHunks.add(new ParsedHunk(before, after, addedLines));
            }

            if (additions != file.additions()
                    || deletions != file.deletions()
                    || additions + deletions != file.changes()) {
                return Optional.empty();
            }

            return Optional.of(new ParsedPatch(parsedHunks, additions, deletions));
        } catch (RuntimeException ignored) {
            return Optional.empty();
        }
    }

    private static Optional<String> normalize(String patch) {
        List<String> normalized = new ArrayList<>();
        boolean insideHunk = false;

        for (String line : patch.lines().toList()) {
            if (line.startsWith("@@")) {
                Matcher matcher = HUNK_HEADER.matcher(line);
                if (!matcher.matches()) {
                    return Optional.empty();
                }
                String oldCount = matcher.group(2) == null ? "1" : matcher.group(2);
                String newCount = matcher.group(4) == null ? "1" : matcher.group(4);
                normalized.add("@@ -%s,%s +%s,%s @@".formatted(
                        matcher.group(1), oldCount, matcher.group(3), newCount
                ));
                insideHunk = true;
            } else if ("\\ No newline at end of file".equals(line)) {
                if (!insideHunk) {
                    return Optional.empty();
                }
            } else if (!insideHunk || line.isEmpty() || " +-".indexOf(line.charAt(0)) < 0) {
                return Optional.empty();
            } else {
                normalized.add(encodeContentLine(line));
            }
        }

        return insideHunk ? Optional.of(String.join("\n", normalized)) : Optional.empty();
    }

    private static String encodeContentLine(String line) {
        char marker = line.charAt(0) == ' ' ? '=' : line.charAt(0);
        String encoded = Base64.getUrlEncoder().withoutPadding()
                .encodeToString(line.substring(1).getBytes(StandardCharsets.UTF_8));
        return marker + encoded;
    }

    private static String decode(String value) {
        return new String(Base64.getUrlDecoder().decode(value), StandardCharsets.UTF_8);
    }

    record ParsedPatch(List<ParsedHunk> hunks, int additions, int deletions) {
        ParsedPatch {
            hunks = List.copyOf(hunks);
        }

        Optional<List<LineChange>> alignedChanges() {
            List<LineChange> changes = new ArrayList<>();
            for (ParsedHunk hunk : hunks) {
                if (hunk.before().size() != hunk.after().size()) {
                    return Optional.empty();
                }
                for (int index = 0; index < hunk.before().size(); index++) {
                    String before = hunk.before().get(index);
                    String after = hunk.after().get(index);
                    if (!before.equals(after)) {
                        changes.add(new LineChange(before, after));
                    }
                }
            }
            return Optional.of(List.copyOf(changes));
        }

        List<String> addedLines() {
            return hunks.stream()
                    .flatMap(hunk -> hunk.addedLines().stream())
                    .toList();
        }
    }

    record ParsedHunk(List<String> before, List<String> after, List<String> addedLines) {
        ParsedHunk {
            before = List.copyOf(before);
            after = List.copyOf(after);
            addedLines = List.copyOf(addedLines);
        }
    }

    record LineChange(String before, String after) {
    }
}
