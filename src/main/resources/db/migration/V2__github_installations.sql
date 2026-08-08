CREATE TABLE github_installations
(
    installation_id         BIGINT PRIMARY KEY,
    app_id                  BIGINT                   NOT NULL,
    app_slug                VARCHAR(255)             NOT NULL DEFAULT '',
    target_id               BIGINT,
    account_id              BIGINT                   NOT NULL,
    account_login           VARCHAR(255)             NOT NULL,
    account_type            VARCHAR(50)              NOT NULL,
    repository_selection    VARCHAR(50)              NOT NULL,
    permissions             JSONB                    NOT NULL DEFAULT '{}'::jsonb,
    status                  VARCHAR(20)              NOT NULL,
    github_created_at       TIMESTAMPTZ,
    github_updated_at       TIMESTAMPTZ,
    permissions_accepted_at TIMESTAMPTZ,
    suspended_at            TIMESTAMPTZ,
    deleted_at              TIMESTAMPTZ,
    last_event_action       VARCHAR(50)              NOT NULL,
    last_delivery_id        VARCHAR(64)              NOT NULL DEFAULT '',
    created_at              TIMESTAMPTZ              NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at              TIMESTAMPTZ              NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT github_installations_status_check
        CHECK (status IN ('active', 'suspended', 'deleted'))
);

CREATE INDEX idx_github_installations_status ON github_installations (status);
CREATE INDEX idx_github_installations_account ON github_installations (account_login);
