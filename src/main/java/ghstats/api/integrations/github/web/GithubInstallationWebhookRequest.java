package ghstats.api.integrations.github.web;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.OffsetDateTime;
import java.util.Map;

record GithubInstallationWebhookRequest(
        @JsonProperty("action") String action,
        @JsonProperty("installation") Installation installation
) {
    record Installation(
            @JsonProperty("id") Long id,
            @JsonProperty("app_id") Long appId,
            @JsonProperty("app_slug") String appSlug,
            @JsonProperty("target_id") Long targetId,
            @JsonProperty("account") Account account,
            @JsonProperty("repository_selection") String repositorySelection,
            @JsonProperty("permissions") Map<String, String> permissions,
            @JsonProperty("created_at") OffsetDateTime createdAt,
            @JsonProperty("updated_at") OffsetDateTime updatedAt,
            @JsonProperty("suspended_at") OffsetDateTime suspendedAt
    ) {
        record Account(
                @JsonProperty("id") Long id,
                @JsonProperty("login") String login,
                @JsonProperty("type") String type
        ) {
        }
    }
}
