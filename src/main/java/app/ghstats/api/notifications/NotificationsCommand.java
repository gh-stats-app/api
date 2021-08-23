package app.ghstats.api.notifications;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

public class NotificationsCommand {

    private final WebClient webClient;
    private final NotificationsConfigurationProperties configurationProperties;

    public NotificationsCommand(WebClient notificationWebClient, NotificationsConfigurationProperties configurationProperties) {
        this.webClient = notificationWebClient;
        this.configurationProperties = configurationProperties;
    }

    public Mono<Void> slackMessage(String message) {
        return webClient.post()
                .uri(configurationProperties.slack().webhook())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(BodyInserters.fromValue(toJson(Map.of("text", message))))
                .retrieve()
                .bodyToMono(Void.class);
    }

    private String toJson(Map<String, Object> data) {
        try {
            return new ObjectMapper().writeValueAsString(data);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("error creating json", e);
        }
    }
}
