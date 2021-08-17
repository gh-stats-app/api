package app.ghstats.api.badges;

import app.ghstats.api.actions.ActionsQuery;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
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

    private final ActionsQuery actionsQuery;
    private final WebClient webClient;

    BadgesController(ActionsQuery actionsQuery) {
        this.actionsQuery = actionsQuery;
        this.webClient = WebClient.builder().build();
    }

    @RequestMapping
    public Mono<ResponseEntity<String>> actionBadge(@RequestParam("action") String actionId, @RequestParam(value = "color", defaultValue = "brightgreen") String color) {
        return actionsQuery
                .getUsage(actionId)
                .map(usage -> webClient.get().uri(getBadgeUrl(color, usage)).header(HttpHeaders.USER_AGENT, "gh-stats.app").retrieve())
                .flatMap(it -> it.bodyToMono(String.class))
                .map(svg -> ResponseEntity.ok()
                        .header(HttpHeaders.CACHE_CONTROL, "max-age=60, public")
                        .header(HttpHeaders.CONTENT_TYPE, "image/svg+xml")
                        .body(svg)
                );
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
