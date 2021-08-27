package app.ghstats.api.badges.web;

import app.ghstats.api.actions.api.ActionId;
import app.ghstats.api.badges.BadgesQuery;
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
import reactor.core.publisher.Mono;

import java.time.Duration;

@RestController
@RequestMapping("/badge")
class BadgesController {

    private final BadgesQuery badgesQuery;

    BadgesController(BadgesQuery badgesQuery) {
        this.badgesQuery = badgesQuery;
    }

    @GetMapping(params = "action")
    public Mono<ResponseEntity<String>> actionBadge(@RequestParam("action") ActionId actionId,
                                                    @RequestParam(value = "color", defaultValue = "brightgreen") String color,
                                                    ServerHttpRequest request) {
        MultiValueMap<String, String> restQueryParams = new LinkedMultiValueMap<>(request.getQueryParams());
        restQueryParams.remove("action");
        restQueryParams.remove("color");
        return badgesQuery.getActionsBadge(actionId, color, restQueryParams)
                .map(svg -> ResponseEntity.ok()
                        .header(HttpHeaders.CACHE_CONTROL, CacheControl.maxAge(Duration.ofSeconds(60)).cachePublic().getHeaderValue())
                        .header(HttpHeaders.CONTENT_TYPE, "image/svg+xml")
                        .body(svg)
                );
    }

    @GetMapping(params = "user")
    public Mono<ResponseEntity<String>> userBadge(@RequestParam("user") String user,
                                                  @RequestParam(value = "color", defaultValue = "brightgreen") String color,
                                                  ServerHttpRequest request) {
        MultiValueMap<String, String> restQueryParams = new LinkedMultiValueMap<>(request.getQueryParams());
        restQueryParams.remove("user");
        restQueryParams.remove("color");
        return badgesQuery.getUserBadge(user, color, restQueryParams)
                .map(svg -> ResponseEntity.ok()
                        .header(HttpHeaders.CACHE_CONTROL, CacheControl.maxAge(Duration.ofSeconds(60)).cachePublic().getHeaderValue())
                        .header(HttpHeaders.CONTENT_TYPE, "image/svg+xml")
                        .body(svg)
                );
    }
}
