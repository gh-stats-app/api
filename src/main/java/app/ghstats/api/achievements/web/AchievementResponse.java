package app.ghstats.api.achievements.web;

import java.net.URI;

record AchievementResponse(String id, String description, URI image, URI icon) {

}
