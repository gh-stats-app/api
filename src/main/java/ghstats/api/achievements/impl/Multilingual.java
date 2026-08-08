package ghstats.api.achievements.impl;

import ghstats.api.achievements.api.AchievementUnlocked;
import ghstats.api.achievements.api.PullRequestContext;
import ghstats.api.achievements.api.UnlockableAchievement;
import ghstats.api.integrations.github.api.GitCommit;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
class Multilingual implements UnlockableAchievement {

    private static final Set<String> LANGUAGE_EXTENSIONS = Set.of(
            ".java", ".py", ".js", ".ts", ".rb", ".go", ".rs", ".cpp", ".c", ".cs",
            ".php", ".swift", ".kt", ".scala", ".clj", ".hs", ".lua", ".r", ".pl",
            ".sh", ".sql", ".html", ".css", ".dart", ".ex", ".erl"
    );

    @Override
    public String getId() {
        return "multilingual";
    }

    @Override
    public String getName() {
        return "Multilingual";
    }

    @Override
    public String getDescription() {
        return "Add/edit files in 3+ different languages in a single commit";
    }

    @Override
    public Optional<AchievementUnlocked> unlock(PullRequestContext context) {
        Set<String> languages = Stream.of(context.addedFiles(), context.modifiedFiles())
                .flatMap(Collection::stream)
                .map(this::getExtension)
                .filter(LANGUAGE_EXTENSIONS::contains)
                .collect(Collectors.toSet());
        return languages.size() >= 3
                ? Optional.of(new AchievementUnlocked(this, context.recipient().userName(), context.triggerCommit()))
                : Optional.empty();
    }

    private String getExtension(String filename) {
        int lastDot = filename.lastIndexOf('.');
        return lastDot >= 0 ? filename.substring(lastDot) : "";
    }
}
