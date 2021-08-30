package app.ghstats.api.achievements.api;

import com.github.slugify.Slugify;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface Achievement {

    Slugify SLUGIFY = new Slugify();

    String getId();

    String getName();

    String getDescription();

    Optional<AchievementUnlocked> unlock(List<GitCommit> commits);

    default URI getImage() {
        return UriComponentsBuilder
                .fromPath("/img/{filename}@6x.png")
                .buildAndExpand(Map.of("filename", SLUGIFY.slugify(getId())))
                .toUri();
    }

    default URI getIcon() {
        return UriComponentsBuilder
                .fromPath("/img/{filename}.png")
                .buildAndExpand(Map.of("filename", SLUGIFY.slugify(getId())))
                .toUri();
    }
}
