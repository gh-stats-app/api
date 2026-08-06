CREATE TABLE achievements_unlocked
(
    id             SERIAL PRIMARY KEY,
    achievement_id VARCHAR(255)                        NOT NULL,
    "user"         VARCHAR(255)                        NOT NULL,
    commit_id      VARCHAR(255)                        NOT NULL,
    url            VARCHAR(240)                        NOT NULL DEFAULT '',
    created_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT uniq_achievements_unlocked_user_achievement_id UNIQUE ("user", achievement_id)
);

CREATE INDEX idx_achievements_unlocked_achievement_id ON achievements_unlocked (achievement_id);
CREATE INDEX idx_achievements_unlocked_user ON achievements_unlocked ("user");
