package ghstats.api.integrations.github.installations;

import java.time.OffsetDateTime;
import java.util.Map;

public record GithubInstallation(
        long id,
        long appId,
        String appSlug,
        Long targetId,
        Account account,
        String repositorySelection,
        Map<String, String> permissions,
        Status status,
        OffsetDateTime githubCreatedAt,
        OffsetDateTime githubUpdatedAt,
        OffsetDateTime permissionsAcceptedAt,
        OffsetDateTime suspendedAt,
        OffsetDateTime deletedAt,
        String lastEventAction,
        String lastDeliveryId
) {
    public GithubInstallation {
        appSlug = valueOrEmpty(appSlug);
        repositorySelection = valueOrEmpty(repositorySelection);
        permissions = permissions == null ? Map.of() : Map.copyOf(permissions);
        lastDeliveryId = valueOrEmpty(lastDeliveryId);
    }

    private static String valueOrEmpty(String value) {
        return value == null ? "" : value;
    }

    public record Account(long id, String login, String type) {
        public Account {
            type = valueOrEmpty(type);
        }
    }

    public enum Status {
        ACTIVE("active"),
        SUSPENDED("suspended"),
        DELETED("deleted");

        private final String databaseValue;

        Status(String databaseValue) {
            this.databaseValue = databaseValue;
        }

        String databaseValue() {
            return databaseValue;
        }
    }
}
