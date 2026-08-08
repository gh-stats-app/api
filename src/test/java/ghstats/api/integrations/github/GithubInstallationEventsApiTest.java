package ghstats.api.integrations.github;

import ghstats.api.BaseIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.r2dbc.core.DatabaseClient;

import static org.assertj.core.api.Assertions.assertThat;

class GithubInstallationEventsApiTest extends BaseIntegrationTest {

    @Autowired
    DatabaseClient databaseClient;

    @Test
    void shouldNotExposeInstallationStatusWithoutSecurity() {
        webClient.get()
                .uri("/integrations/github/installations/status")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void shouldStoreInstallationAndTrackPermissionAcceptance() {
        postInstallationEvent("delivery-created", installationEvent("created", "read", null));

        var created = storedInstallation();
        assertThat(created.accountLogin()).isEqualTo("gh-stats-app");
        assertThat(created.status()).isEqualTo("active");
        assertThat(created.permissions()).contains("\"pull_requests\": \"read\"");
        assertThat(created.lastDeliveryId()).isEqualTo("delivery-created");

        postInstallationEvent(
                "delivery-permissions",
                installationEvent("new_permissions_accepted", "write", null)
        );

        var updated = storedInstallation();
        assertThat(updated.permissions()).contains("\"pull_requests\": \"write\"");
        assertThat(updated.permissionsAcceptedAt()).isNotNull();
        assertThat(updated.lastEventAction()).isEqualTo("new_permissions_accepted");

    }

    @Test
    void shouldTrackSuspendedAndDeletedInstallations() {
        postInstallationEvent("delivery-created", installationEvent("created", "write", null));
        postInstallationEvent(
                "delivery-suspended",
                installationEvent("suspend", "write", "2026-08-07T14:00:00Z")
        );

        assertThat(storedInstallation().status()).isEqualTo("suspended");

        postInstallationEvent("delivery-deleted", installationEvent("deleted", "write", null));

        assertThat(storedInstallation().status()).isEqualTo("deleted");
    }

    @Test
    void shouldStorePermissionSetsChosenByDifferentInstallations() {
        postInstallationEvent(
                "delivery-old-permissions",
                installationEvent(151893953L, "old-org", "created", "read", null)
        );
        postInstallationEvent(
                "delivery-new-permissions",
                installationEvent(151893954L, "updated-org", "created", "write", null)
        );

        Long permissionSets = databaseClient.sql("""
                        SELECT COUNT(DISTINCT permissions)
                        FROM github_installations
                        WHERE status <> 'deleted'
                        """)
                .map(row -> row.get(0, Long.class))
                .one()
                .block();

        assertThat(permissionSets).isEqualTo(2);
    }

    private void postInstallationEvent(String deliveryId, String payload) {
        webClient.post()
                .uri("/integrations/github/events")
                .header("X-GitHub-Event", "installation")
                .header("X-GitHub-Delivery", deliveryId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(payload)
                .exchange()
                .expectStatus().isAccepted();
    }

    private StoredInstallation storedInstallation() {
        return databaseClient.sql("""
                        SELECT account_login, status, permissions::text AS permissions,
                               permissions_accepted_at, last_event_action, last_delivery_id
                        FROM github_installations
                        WHERE installation_id = 151893953
                        """)
                .map(row -> new StoredInstallation(
                        row.get("account_login", String.class),
                        row.get("status", String.class),
                        row.get("permissions", String.class),
                        row.get("permissions_accepted_at", java.time.OffsetDateTime.class),
                        row.get("last_event_action", String.class),
                        row.get("last_delivery_id", String.class)
                ))
                .one()
                .block();
    }

    private String installationEvent(String action, String pullRequestsPermission, String suspendedAt) {
        return installationEvent(151893953L, "gh-stats-app", action, pullRequestsPermission, suspendedAt);
    }

    private String installationEvent(
            long installationId,
            String accountLogin,
            String action,
            String pullRequestsPermission,
            String suspendedAt
    ) {
        String suspendedAtJson = suspendedAt == null ? "null" : "\"%s\"".formatted(suspendedAt);
        return """
                {
                  "action": "%s",
                  "installation": {
                    "id": %d,
                    "app_id": 143440,
                    "app_slug": "gh-stats",
                    "target_id": 104277310,
                    "account": {
                      "id": 104277310,
                      "login": "%s",
                      "type": "Organization"
                    },
                    "repository_selection": "selected",
                    "permissions": {
                      "metadata": "read",
                      "pull_requests": "%s"
                    },
                    "created_at": "2026-08-07T09:32:27Z",
                    "updated_at": "2026-08-07T14:00:00Z",
                    "suspended_at": %s
                  }
                }
                """.formatted(action, installationId, accountLogin, pullRequestsPermission, suspendedAtJson);
    }

    private record StoredInstallation(
            String accountLogin,
            String status,
            String permissions,
            java.time.OffsetDateTime permissionsAcceptedAt,
            String lastEventAction,
            String lastDeliveryId
    ) {
    }
}
