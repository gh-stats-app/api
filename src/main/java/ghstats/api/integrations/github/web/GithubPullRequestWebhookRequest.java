package ghstats.api.integrations.github.web;

import com.fasterxml.jackson.annotation.JsonProperty;

record GithubPullRequestWebhookRequest(
        @JsonProperty("pull_request") PullRequest pullRequest,
        @JsonProperty("repository") Repository repository,
        @JsonProperty("installation") Installation installation
) {
    record PullRequest(
            @JsonProperty("merged") Boolean merged,
            @JsonProperty("html_url") String htmlUrl,
            @JsonProperty("user") User user,
            @JsonProperty("head") Head head
    ) {
        record User(
                @JsonProperty("login") String login,
                @JsonProperty("type") String type
        ) {
        }

        record Head(
                @JsonProperty("sha") String sha
        ) {
        }
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
