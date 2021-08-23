package app.ghstats.api.badges;

import app.ghstats.api.actions.ActionsQuery;
import app.ghstats.api.actions.api.ActionId;
import org.springframework.http.HttpHeaders;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Map;

public class BadgesQuery {
    private final ActionsQuery actionsQuery;
    private final WebClient webClient;

    public BadgesQuery(ActionsQuery actionsQuery, WebClient webClient) {
        this.actionsQuery = actionsQuery;
        this.webClient = webClient;
    }

    public Mono<String> getActionsBadge(ActionId actionId, String color, MultiValueMap<String, String> queryParams) {
        return actionsQuery
                .getUsageCount(actionId)
                .map(usage -> queryShieldsIo(color, queryParams, usage))
                .flatMap(it -> it.bodyToMono(String.class));

    }

    private WebClient.ResponseSpec queryShieldsIo(String color, MultiValueMap<String, String> queryParams, Long usage) {
        URI target = UriComponentsBuilder
                .fromHttpUrl("https://img.shields.io/badge/{label}-{message}-{color}")
                .queryParams(queryParams)
                .buildAndExpand(Map.of("label", "Used ", "message", "%d times".formatted(usage), "color", color))
                .toUri();
        return webClient.get().uri(target)
                .header(HttpHeaders.USER_AGENT, "gh-stats.app")
                .retrieve();
    }
}
