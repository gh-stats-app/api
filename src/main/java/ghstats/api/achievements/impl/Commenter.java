package ghstats.api.achievements.impl;

import ghstats.api.achievements.api.AchievementUnlocked;
import ghstats.api.achievements.api.PullRequestContext;
import ghstats.api.achievements.api.UnlockableAchievement;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Component
class Commenter implements UnlockableAchievement {

    @Override
    public String getId() {
        return "commenter";
    }

    @Override
    public String getName() {
        return "Commenter";
    }

    @Override
    public String getDescription() {
        return "Only add a comment";
    }

    @Override
    public Optional<AchievementUnlocked> unlock(PullRequestContext context) {
        boolean addsOnlyComments = context.files().stream().anyMatch(file -> GitHubFilePatch.parse(file)
                .filter(patch -> patch.additions() > 0 && patch.deletions() == 0)
                .filter(patch -> commentSyntax(file.filename())
                        .map(syntax -> syntax.containsOnlyComments(patch.addedLines()))
                        .orElse(false))
                .isPresent());
        return addsOnlyComments
                ? Optional.of(new AchievementUnlocked(this, context.recipient().userName(), context.triggerCommit()))
                : Optional.empty();
    }

    private Optional<CommentSyntax> commentSyntax(String filename) {
        String lower = filename.toLowerCase(Locale.ROOT);
        if (hasExtension(lower, ".java", ".js", ".jsx", ".ts", ".tsx", ".c", ".h", ".cpp", ".cc",
                ".cxx", ".hpp", ".cs", ".go", ".rs", ".kt", ".kts", ".swift", ".scala", ".groovy")) {
            return Optional.of(new CommentSyntax(List.of("//"), "/*", "*/"));
        }
        if (hasExtension(lower, ".css", ".scss", ".less")) {
            return Optional.of(new CommentSyntax(List.of(), "/*", "*/"));
        }
        if (hasExtension(lower, ".py", ".rb", ".sh", ".bash", ".zsh", ".yml", ".yaml", ".toml", ".pl",
                ".pm", ".r") || lower.endsWith("dockerfile")) {
            return Optional.of(new CommentSyntax(List.of("#"), null, null));
        }
        if (hasExtension(lower, ".sql", ".lua", ".hs")) {
            return Optional.of(new CommentSyntax(List.of("--"), "/*", "*/"));
        }
        if (hasExtension(lower, ".xml", ".html", ".htm", ".md")) {
            return Optional.of(new CommentSyntax(List.of(), "<!--", "-->"));
        }
        if (hasExtension(lower, ".ini", ".properties")) {
            return Optional.of(new CommentSyntax(List.of("#", ";"), null, null));
        }
        return Optional.empty();
    }

    private boolean hasExtension(String filename, String... extensions) {
        for (String extension : extensions) {
            if (filename.endsWith(extension)) {
                return true;
            }
        }
        return false;
    }

    private record CommentSyntax(List<String> linePrefixes, String blockStart, String blockEnd) {
        boolean containsOnlyComments(List<String> lines) {
            boolean insideBlock = false;
            for (String line : lines) {
                String trimmed = line.trim();
                if (insideBlock) {
                    int end = trimmed.indexOf(blockEnd);
                    if (end >= 0) {
                        if (!trimmed.substring(end + blockEnd.length()).isBlank()) {
                            return false;
                        }
                        insideBlock = false;
                    }
                } else if (!linePrefixes.stream().anyMatch(trimmed::startsWith)
                        && blockStart != null && trimmed.startsWith(blockStart)) {
                    int end = trimmed.indexOf(blockEnd, blockStart.length());
                    if (end < 0) {
                        insideBlock = true;
                    } else if (!trimmed.substring(end + blockEnd.length()).isBlank()) {
                        return false;
                    }
                } else if (!linePrefixes.stream().anyMatch(trimmed::startsWith)) {
                    return false;
                }
            }
            return !insideBlock;
        }
    }
}
