package ghstats.api.services.github;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.ZonedDateTime;

record PrCommitResponse(
        @JsonProperty("sha") String sha,
        @JsonProperty("commit") CommitData commit,
        @JsonProperty("author") GitHubUser author,
        @JsonProperty("html_url") String htmlUrl
) {
    record CommitData(
            @JsonProperty("message") String message,
            @JsonProperty("author") AuthorData author
    ) {
        record AuthorData(
                @JsonProperty("date") ZonedDateTime date
        ) {
        }
    }

    record GitHubUser(
            @JsonProperty("login") String login
    ) {
    }
}
