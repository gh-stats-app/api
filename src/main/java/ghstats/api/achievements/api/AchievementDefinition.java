package ghstats.api.achievements.api;

import com.github.slugify.Slugify;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Map;

public interface AchievementDefinition {

    Slugify SLUGIFY = new Slugify();

    default String getId() {
        return SLUGIFY.slugify(this.getName());
    }

    String getName();

    String getDescription();

    default URI getImage() {
        return UriComponentsBuilder
                .fromPath("/img/{filename}@6x.png")
                .buildAndExpand(Map.of("filename", this.getId()))
                .toUri();
    }

    default URI getIcon() {
        return UriComponentsBuilder
                .fromPath("/img/{filename}.png")
                .buildAndExpand(Map.of("filename", this.getId()))
                .toUri();
    }
}
