package app.ghstats.api.achievements.api;

import com.github.slugify.Slugify;

import java.util.List;
import java.util.Optional;

public interface Achievement {

    Slugify SLUGIFY = new Slugify();

    default String getId() {
        return SLUGIFY.slugify(getName());
    }

    String getName();

    String getDescription();

    default String getImage() {
        return "default.png";
    }

    Optional<AchievementUnlocked> check(List<GitCommit> commits);
}
