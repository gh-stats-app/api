package app.ghstats.api.services.mailgun;

import app.ghstats.api.achievements.api.AchievementUnlocked;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;

public class MailgunClient {

    static final String TARGET_PATH = "/messages";

    private final String apiUrl;
    private final String apiKey;
    private final WebClient webClient;

    MailgunClient(WebClient webClient, MailgunConfigurationProperties configurationProperties) {
        this.apiKey = configurationProperties.apiKey();
        this.apiUrl = configurationProperties.apiUrl();
        this.webClient = webClient;
    }

    public Mono<String> sendUnlockedMessage(AchievementUnlocked achievementUnlocked) {
        URI uri = UriComponentsBuilder.fromUriString(apiUrl).path(TARGET_PATH).build().toUri();
        MultiValueMap<String, String> context = new LinkedMultiValueMap<>();
        context.add("from", "gh-stats.app <no-reply@gh-stats.app>");
        context.add("to", achievementUnlocked.commit().author().userEmail().value());
        context.add("subject", "🏆 '%s' - Achievement Unlocked!".formatted(achievementUnlocked.achievement().getName()));
        context.add("text", """
                Congratulations!
                                
                With your commit (%s) you unlocked '%s' achievement!
                Keep up the good work!
                """.formatted(achievementUnlocked.commit().id(), achievementUnlocked.achievement().getName()));
        return webClient.post()
                .uri(uri)
                .headers(headers -> headers.setBasicAuth("api", apiKey))
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .body(BodyInserters.fromFormData(context))
                .retrieve()
                .bodyToMono(String.class);
    }
}
