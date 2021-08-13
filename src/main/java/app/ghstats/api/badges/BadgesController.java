package app.ghstats.api.badges;

import app.ghstats.api.actions.ActionsQuery;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Map;

@RestController
@RequestMapping("/badge")
class BadgesController {

    private final ActionsQuery actionsStatsQuery;
    private final WebClient webClient;

    BadgesController(ActionsQuery actionsStatsQuery) {
        this.actionsStatsQuery = actionsStatsQuery;
        this.webClient = WebClient.builder().build();
    }

    @RequestMapping
    public Mono<String> actionBadge(@RequestParam("action") String actionId, @RequestParam(value = "color", defaultValue = "brightgreen") String color) {
        return actionsStatsQuery
                .getUsage(actionId)
                .map(usage -> webClient.get()
                        .uri(getBadgeUrl(color, usage))
                        .header(HttpHeaders.CACHE_CONTROL, "public, max-age 60")
                        .header(HttpHeaders.CONTENT_TYPE, "image/svg+xml")
                        .retrieve())
                .flatMap(it -> it.bodyToMono(String.class));
    }

    private URI getBadgeUrl(String color, Long usage) {
        return UriComponentsBuilder
                .fromHttpUrl("https://img.shields.io/badge/{label}-{message}-{color}")
                .buildAndExpand(Map.of(
                        "label", "Used ",
                        "message", "%d times".formatted(usage),
                        "color", color
                ))
                .toUri();
    }
}
