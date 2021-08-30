package app.ghstats.api.services.slack;

import app.ghstats.api.achievements.api.Achievement;
import app.ghstats.api.achievements.api.AchievementUnlocked;
import app.ghstats.api.achievements.api.CommitAuthor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
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
        Achievement achievement = achievementUnlocked.achievement();
        return webClient.post()
                .uri(configurationProperties.webhook())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(BodyInserters.fromValue(toJson(Map.of(
                        "blocks", List.of(
                                Map.of(
                                        "type", "section",
                                        "text", Map.of(
                                                "type", "mrkdwn",
                                                "text", "Achievement *%s* unlocked by <@%s>!\n\n>%s\n(%s)\n\n_%s_".formatted(achievement.getName(), author.userName(), achievementUnlocked.commit().message(), achievementUnlocked.commit().id(), achievement.getDescription())
                                        ),
                                        "accessory", Map.of(
                                                "type", "image",
                                                "image_url", "https://gh-stats.app/%s".formatted(achievement.getImage()),
                                                "alt_text", achievement.getId()
                                        )
                                ))
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