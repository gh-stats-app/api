package ghstats.api.achievements.web;

import java.net.URI;

record AchievementDefinitionResponse(
        String id,
        String description,
        URI image,
        URI icon
) {

}
