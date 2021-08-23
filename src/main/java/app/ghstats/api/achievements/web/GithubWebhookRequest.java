package app.ghstats.api.achievements.web;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.ZonedDateTime;
import java.util.List;

record GithubWebhookRequest(
        @JsonProperty("commits") List<GithubCommitRequestItem> commits
) {
    static record GithubCommitRequestItem(
            @JsonProperty("id") String id,
            @JsonProperty("author") GithubAuthor author,
            @JsonProperty("message") String message,
            @JsonProperty("timestamp") ZonedDateTime timestamp,
            @JsonProperty("added") List<String> added,
            @JsonProperty("removed") List<String> removed,
            @JsonProperty("modified") List<String> modified
    ) {
        static record GithubAuthor(
                @JsonProperty("username") String username
        ) {
        }
    }
}
