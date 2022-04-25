package ghstats.api.services.shields;

import org.springframework.http.HttpHeaders;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Map;

public class ShieldsClient {
    private final WebClient webClient;

    ShieldsClient(WebClient webClient) {
        this.webClient = webClient;
    }

    public Mono<String> getShield(String label, String message, String color, MultiValueMap<String, String> options) {
        URI target = UriComponentsBuilder
                .fromHttpUrl("https://img.shields.io/badge/{label}-{message}-{color}")
                .queryParams(options)
                .buildAndExpand(Map.of("label", label, "message", message, "color", color))
                .toUri();
        return webClient.get().uri(target)
                .header(HttpHeaders.USER_AGENT, "gh-stats.app")
                .retrieve()
                .bodyToMono(String.class);
    }
}
