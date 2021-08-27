package app.ghstats.api.achievements.api;

import com.github.slugify.Slugify;

import java.util.List;
import java.util.Optional;

public interface Achievement {

    Slugify SLUGIFY = new Slugify();

    String getName();

    String getDescription();

    Optional<AchievementUnlocked> check(List<GitCommit> commits);

    default String getId() {
        return SLUGIFY.slugify(getName());
    }

    default String getImage() {
        return "%s@6x.png".formatted(SLUGIFY.slugify(getId()));
    }
}
