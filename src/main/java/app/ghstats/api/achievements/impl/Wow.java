package app.ghstats.api.achievements.impl;

import app.ghstats.api.achievements.api.Achievement;
import app.ghstats.api.achievements.api.AchievementUnlocked;
import app.ghstats.api.achievements.api.GitCommit;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@Component
class Wow implements Achievement {

    public static final Pattern WOW_PATTERN = Pattern.compile("\\bwow\\b", Pattern.CASE_INSENSITIVE);

    @Override
    public String getName() {
        return "Wow";
    }

    @Override
    public String getDescription() {
        return "use word “wow” in a commit message";
    }

    @Override
    public String getImage() {
        return "wow.png";
    }

    @Override
    public Optional<AchievementUnlocked> check(List<GitCommit> commits) {
        return commits.stream()
                .filter(it -> WOW_PATTERN.matcher(it.message()).find())
                .findFirst()
                .map(commit -> new AchievementUnlocked(commit.id(), commit.userName()));
    }
}
