package app.ghstats.api.achievements.impl;

import app.ghstats.api.achievements.api.CommitId;
import app.ghstats.api.achievements.api.GitCommit;
import app.ghstats.api.achievements.api.UserName;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

abstract class AchievementTest {
    GitCommit commit(String message) {
        return commit(UUID.randomUUID().toString(), message);
    }

    GitCommit commit(String id, String message) {
        return new GitCommit(CommitId.valueOf(id), UserName.valueOf("bgalek"), message, ZonedDateTime.now(), List.of(), List.of(), List.of());
    }
}
