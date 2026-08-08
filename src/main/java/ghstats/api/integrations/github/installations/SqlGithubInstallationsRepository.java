package ghstats.api.integrations.github.installations;

import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Mono;
import tools.jackson.databind.ObjectMapper;

import java.time.OffsetDateTime;

class SqlGithubInstallationsRepository implements GithubInstallationsRepository {

    private static final String UPSERT = """
            INSERT INTO github_installations (
                installation_id, app_id, app_slug, target_id,
                account_id, account_login, account_type, repository_selection,
                permissions, status, github_created_at, github_updated_at,
                permissions_accepted_at, suspended_at, deleted_at,
                last_event_action, last_delivery_id
            ) VALUES (
                $1, $2, $3, $4,
                $5, $6, $7, $8,
                CAST($9 AS jsonb), $10, CAST($11 AS timestamptz), CAST($12 AS timestamptz),
                CAST($13 AS timestamptz), CAST($14 AS timestamptz), CAST($15 AS timestamptz),
                $16, $17
            )
            ON CONFLICT (installation_id) DO UPDATE SET
                app_id = EXCLUDED.app_id,
                app_slug = EXCLUDED.app_slug,
                target_id = EXCLUDED.target_id,
                account_id = EXCLUDED.account_id,
                account_login = EXCLUDED.account_login,
                account_type = EXCLUDED.account_type,
                repository_selection = EXCLUDED.repository_selection,
                permissions = CASE
                    WHEN EXCLUDED.permissions = '{}'::jsonb THEN github_installations.permissions
                    ELSE EXCLUDED.permissions
                END,
                status = EXCLUDED.status,
                github_created_at = COALESCE(EXCLUDED.github_created_at, github_installations.github_created_at),
                github_updated_at = COALESCE(EXCLUDED.github_updated_at, github_installations.github_updated_at),
                permissions_accepted_at = COALESCE(EXCLUDED.permissions_accepted_at, github_installations.permissions_accepted_at),
                suspended_at = EXCLUDED.suspended_at,
                deleted_at = COALESCE(EXCLUDED.deleted_at, github_installations.deleted_at),
                last_event_action = EXCLUDED.last_event_action,
                last_delivery_id = EXCLUDED.last_delivery_id,
                updated_at = CURRENT_TIMESTAMP
            """;

    private final DatabaseClient databaseClient;
    private final ObjectMapper objectMapper;

    SqlGithubInstallationsRepository(DatabaseClient databaseClient, ObjectMapper objectMapper) {
        this.databaseClient = databaseClient;
        this.objectMapper = objectMapper;
    }

    @Override
    public Mono<Void> save(GithubInstallation installation) {
        return Mono.defer(() -> {
            var account = installation.account();
            var execute = databaseClient.sql(UPSERT)
                    .bind("$1", installation.id())
                    .bind("$2", installation.appId())
                    .bind("$3", installation.appSlug())
                    .bind("$5", account.id())
                    .bind("$6", account.login())
                    .bind("$7", account.type())
                    .bind("$8", installation.repositorySelection())
                    .bind("$9", objectMapper.writeValueAsString(installation.permissions()))
                    .bind("$10", installation.status().databaseValue())
                    .bind("$16", installation.lastEventAction())
                    .bind("$17", installation.lastDeliveryId());
            execute = bindNullable(execute, "$4", installation.targetId(), Long.class);
            execute = bindDate(execute, "$11", installation.githubCreatedAt());
            execute = bindDate(execute, "$12", installation.githubUpdatedAt());
            execute = bindDate(execute, "$13", installation.permissionsAcceptedAt());
            execute = bindDate(execute, "$14", installation.suspendedAt());
            execute = bindDate(execute, "$15", installation.deletedAt());
            return execute.fetch().rowsUpdated().then();
        });
    }

    private DatabaseClient.GenericExecuteSpec bindDate(
            DatabaseClient.GenericExecuteSpec execute,
            String name,
            OffsetDateTime value
    ) {
        return bindNullable(execute, name, value == null ? null : value.toString(), String.class);
    }

    private <T> DatabaseClient.GenericExecuteSpec bindNullable(
            DatabaseClient.GenericExecuteSpec execute,
            String name,
            T value,
            Class<T> type
    ) {
        return value == null ? execute.bindNull(name, type) : execute.bind(name, value);
    }
}
