package ghstats.api.integrations.github.web;

import com.fasterxml.jackson.annotation.JsonProperty;

record GithubPullRequestWebhookRequest(
        @JsonProperty("pull_request") PullRequest pullRequest,
        @JsonProperty("repository") Repository repository,
        @JsonProperty("installation") Installation installation
) {
    record PullRequest(
            @JsonProperty("merged") Boolean merged
    ) {
    }

    record Repository(
            @JsonProperty("name") String name,
            @JsonProperty("owner") Owner owner
    ) {
        record Owner(
                @JsonProperty("login") String login
        ) {
        }
    }

    record Installation(
            @JsonProperty("id") Long id
    ) {
    }
}
