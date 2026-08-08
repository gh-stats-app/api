package ghstats.api.achievements.impl;

import ghstats.api.achievements.api.AchievementUnlocked;
import ghstats.api.achievements.api.PullRequestContext;
import ghstats.api.integrations.github.api.GitCommit;
import ghstats.api.achievements.api.UnlockableAchievement;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
class CitationNeeded implements UnlockableAchievement {

    @Override
    public String getId() {
        return "citation-needed";
    }

    @Override
    public String getName() {
        return "Citation Needed";
    }

    @Override
    public String getDescription() {
        return "StackOverflow URL in a commit body or message";
    }

    @Override
    public Optional<AchievementUnlocked> unlock(PullRequestContext context) {
        List<GitCommit> commits = context.commits();
        return commits.stream()
                .filter(it -> it.message().toLowerCase().contains("stackoverflow.com/questions/"))
                .findFirst()
                .map(commit -> new AchievementUnlocked(this, context.recipient().userName(), commit));
    }
}
