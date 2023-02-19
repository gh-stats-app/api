package ghstats.api.services.slack;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ghstats.api.achievements.api.AchievementDefinition;
import ghstats.api.achievements.api.AchievementUnlocked;
import ghstats.api.integrations.github.api.CommitAuthor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

public class SlackClient {
    private final WebClient webClient;
    private final SlackConfigurationProperties configurationProperties;
    private final ObjectMapper objectMapper = new ObjectMapper();

    SlackClient(WebClient webClient, SlackConfigurationProperties configurationProperties) {
        this.webClient = webClient;
        this.configurationProperties = configurationProperties;
    }

    public Mono<Void> sendUnlockedMessage(AchievementUnlocked achievementUnlocked) {
        CommitAuthor author = achievementUnlocked.commit().author();
        AchievementDefinition achievement = achievementUnlocked.achievement();
        String imageUrl = UriComponentsBuilder.fromUriString("https://api.gh-stats.app").path(achievement.getImage().getPath()).build().toUriString();
        return webClient.post()
                .uri(configurationProperties.webhook())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(BodyInserters.fromValue(toJson(Map.of(
                        "blocks", List.of(
                                Map.of(
                                        "type", "section",
                                        "text", Map.of(
                                                "type", "mrkdwn",
                                                "text", "*Good work!*\n\nAchievement *%s* <%s|unlocked> by <@%s>!\n_%s_".formatted(
                                                        achievement.getName(),
                                                        achievementUnlocked.commit().url(),
                                                        author.userName(),
                                                        achievement.getDescription())
                                        ),
                                        "accessory", Map.of(
                                                "type", "image",
                                                "image_url", imageUrl,
                                                "alt_text", achievement.getId()
                                        )
                                ),
                                Map.of(
                                        "type", "context",
                                        "elements", List.of(Map.of(
                                                "type", "mrkdwn",
                                                "text", "Check out all unlockable achievements on <gh-stats.app|https://gh-stats.app>!"
                                        ))
                                )
                        )
                ))))
                .retrieve()
                .bodyToMono(Void.class);
    }

    private String toJson(Map<String, Object> data) {
        try {
            return objectMapper.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("error creating json", e);
        }
    }
}
