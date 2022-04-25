package ghstats.api.badges;

import ghstats.api.actions.ActionsQuery;
import ghstats.api.actions.api.ActionId;
import ghstats.api.services.github.GithubClient;
import ghstats.api.services.shields.ShieldsClient;
import org.ocpsoft.prettytime.PrettyTime;
import org.springframework.util.MultiValueMap;
import reactor.core.publisher.Mono;

public class BadgesQuery {

    private final ActionsQuery actionsQuery;
    private final ShieldsClient shieldsClient;
    private final GithubClient githubClient;

    public BadgesQuery(ActionsQuery actionsQuery, ShieldsClient shieldsClient, GithubClient githubClient) {
        this.actionsQuery = actionsQuery;
        this.shieldsClient = shieldsClient;
        this.githubClient = githubClient;
    }

    public Mono<String> getActionsBadge(ActionId actionId, String color, MultiValueMap<String, String> queryParams) {
        return actionsQuery
                .getUsageCount(actionId)
                .flatMap(usage -> shieldsClient.getShield("Used ", "%d times".formatted(usage), color, queryParams));
    }

    public Mono<String> getActionsBadge(ActionId actionId, String tag, String color, MultiValueMap<String, String> queryParams) {
        return actionsQuery
                .getUsageCount(actionId, tag)
                .flatMap(usage -> shieldsClient.getShield("Used ", "%d times".formatted(usage), color, queryParams));
    }

    public Mono<String> getUserBadge(String user, String color, MultiValueMap<String, String> queryParams) {
        return githubClient
                .getLastCommitDate(user)
                .map(dateTime -> new PrettyTime().format(dateTime))
                .flatMap(lastCommitDateString -> shieldsClient.getShield("Last commit:", lastCommitDateString, color, queryParams));
    }
}
