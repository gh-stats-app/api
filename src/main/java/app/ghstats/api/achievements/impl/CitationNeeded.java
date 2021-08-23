package app.ghstats.api.achievements.impl;

import app.ghstats.api.achievements.api.Achievement;
import app.ghstats.api.achievements.api.AchievementUnlocked;
import app.ghstats.api.achievements.api.GitCommit;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

@Component
class CitationNeeded implements Achievement {

    @Override
    public String getName() {
        return "Citation Needed";
    }

    @Override
    public String getDescription() {
        return "StackOverflow URL in a commit body or message";
    }

    @Override
    public String getImage() {
        return "wow.png";
    }

    @Override
    public Optional<AchievementUnlocked> check(List<GitCommit> commits) {
        return commits.stream()
                .filter(it -> it.message().toLowerCase().contains("stackoverflow.com/questions/"))
                .findFirst()
                .map(commit -> new AchievementUnlocked(commit.id(), commit.userName()));
    }
}
