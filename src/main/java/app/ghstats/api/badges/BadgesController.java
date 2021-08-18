package app.ghstats.api.badges;

import app.ghstats.api.actions.ActionsQuery;
import app.ghstats.api.domain.ActionId;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.time.Duration;
import java.util.List;
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

    @GetMapping
    public Mono<ResponseEntity<String>> actionBadge(@RequestParam("action") ActionId actionId, ServerHttpRequest request) {
        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>(request.getQueryParams());
        queryParams.remove("action");
        return actionsQuery
                .getUsage(actionId)
                .map(usage -> webClient.get().uri(getBadgeUrl(usage, queryParams)).header(HttpHeaders.USER_AGENT, "gh-stats.app").retrieve())
                .flatMap(it -> it.bodyToMono(String.class))
                .map(svg -> ResponseEntity.ok()
                        .header(HttpHeaders.CACHE_CONTROL, CacheControl.maxAge(Duration.ofSeconds(60)).cachePublic().getHeaderValue())
                        .header(HttpHeaders.CONTENT_TYPE, "image/svg+xml")
                        .body(svg)
                );
    }

    private URI getBadgeUrl(Long usage, MultiValueMap<String, String> queryParams) {
        return UriComponentsBuilder
                .fromHttpUrl("https://img.shields.io/badge/{label}-{message}-{color}")
                .queryParams(queryParams)
                .buildAndExpand(Map.of(
                        "label", "Used ",
                        "message", "%d times".formatted(usage),
                        "color", queryParams.getOrDefault("color", List.of("brightgreen"))
                ))
                .toUri();
    }
}
