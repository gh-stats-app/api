package ghstats.api.integrations.github;

import ghstats.api.BaseIntegrationTest;
import ghstats.api.services.github.GithubClient;
import ghstats.api.integrations.github.api.CommitAuthor;
import ghstats.api.integrations.github.api.CommitId;
import ghstats.api.integrations.github.api.GitCommit;
import ghstats.api.integrations.github.api.UserEmail;
import ghstats.api.integrations.github.api.UserName;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class GithubEventsApiTest extends BaseIntegrationTest {

    @MockitoBean
    GithubClient githubClient;

    @Autowired
    MeterRegistry meterRegistry;

    @Test
    @DisplayName("should process merged PR event")
    void testMergedPrEvent() {
        // given
        double processedBefore = counterValue("ghstats.github.pull_request.processed", "result", "unlocks_commented");
        double unlocksBefore = counterValue("ghstats.achievements.unlocks", "achievement", "fix", "result", "created");
        var commit = new GitCommit(
                CommitId.valueOf("abc123"),
                new CommitAuthor(UserName.valueOf("bgalek"), UserEmail.valueOf("bartosz@email.local")),
                "fix: something",
                ZonedDateTime.now(ZoneId.systemDefault()),
                List.of(), List.of(), List.of("wow.txt"),
                URI.create("https://github.com/bgalek/gh-events-test/commit/abc123"),
                new GitCommit.PushMetadata(false, "refs/pull/1/merge")
        );
        Mockito.when(githubClient.fetchPrCommits(12345L, "bgalek", "gh-events-test", 1))
                .thenReturn(Mono.just(List.of(commit)));
        Mockito.when(githubClient.createOrUpdatePrComment(Mockito.eq(12345L), Mockito.eq("bgalek"), Mockito.eq("gh-events-test"), Mockito.eq(1), Mockito.anyString()))
                .thenReturn(Mono.empty());

        // expect
        webClient.post()
                .uri("/integrations/github/events")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(mergedPrEvent))
                .exchange()
                .expectStatus()
                .isAccepted();

        assertThat(counterValue("ghstats.github.pull_request.processed", "result", "unlocks_commented"))
                .isEqualTo(processedBefore + 1);
        assertThat(counterValue("ghstats.achievements.unlocks", "achievement", "fix", "result", "created"))
                .isEqualTo(unlocksBefore + 1);
    }

    @Test
    @DisplayName("should skip non-merged PR event")
    void testNonMergedPrEvent() {
        double skippedBefore = counterValue("ghstats.github.webhook.skipped", "reason", "not_merged");

        webClient.post()
                .uri("/integrations/github/events")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(closedNotMergedPrEvent))
                .exchange()
                .expectStatus()
                .isAccepted();

        Mockito.verifyNoInteractions(githubClient);
        assertThat(counterValue("ghstats.github.webhook.skipped", "reason", "not_merged"))
                .isEqualTo(skippedBefore + 1);
    }

    @Test
    @DisplayName("should accept GitHub ping")
    void testPingEvent() {
        double skippedBefore = counterValue("ghstats.github.webhook.skipped", "reason", "ping");

        webClient.post()
                .uri("/integrations/github/events")
                .header("X-GitHub-Event", "ping")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(pingEvent)
                .exchange()
                .expectStatus()
                .isAccepted();

        Mockito.verifyNoInteractions(githubClient);
        assertThat(counterValue("ghstats.github.webhook.skipped", "reason", "ping"))
                .isEqualTo(skippedBefore + 1);
    }

    @Test
    @DisplayName("should skip unsupported GitHub event without parsing it as PR payload")
    void testUnsupportedEvent() {
        double skippedBefore = counterValue("ghstats.github.webhook.skipped", "reason", "unsupported_event");

        webClient.post()
                .uri("/integrations/github/events")
                .header("X-GitHub-Event", "issues")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(issuesEvent))
                .exchange()
                .expectStatus()
                .isAccepted();

        Mockito.verifyNoInteractions(githubClient);
        assertThat(counterValue("ghstats.github.webhook.skipped", "reason", "unsupported_event"))
                .isEqualTo(skippedBefore + 1);
    }

    @Test
    @DisplayName("should treat push as an unsupported event")
    void testPushEventUnsupported() {
        double skippedBefore = counterValue("ghstats.github.webhook.skipped", "reason", "unsupported_event");

        webClient.post()
                .uri("/integrations/github/events")
                .header("X-GitHub-Event", "push")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(pushEvent))
                .exchange()
                .expectStatus()
                .isAccepted();

        Mockito.verifyNoInteractions(githubClient);
        assertThat(counterValue("ghstats.github.webhook.skipped", "reason", "unsupported_event"))
                .isEqualTo(skippedBefore + 1);
    }

    @Test
    @DisplayName("should preview opened PR event before it is closed")
    void testOpenedPrEvent() {
        double processedBefore = counterValue("ghstats.github.pull_request.processed", "result", "unlocks_commented");
        double unlocksBefore = counterValue("ghstats.achievements.unlocks", "achievement", "windows-language", "result", "created");
        var commit = new GitCommit(
                CommitId.valueOf("aae355515b40af70839312d928a1fc9a5c4f148e"),
                new CommitAuthor(UserName.valueOf("bgalek"), UserEmail.valueOf("bartosz@email.local")),
                "Add echo command to test2.bat",
                ZonedDateTime.now(ZoneId.systemDefault()),
                List.of("test2.bat"), List.of(), List.of(),
                URI.create("https://github.com/gh-stats-app/test-repository/commit/aae355515b40af70839312d928a1fc9a5c4f148e"),
                new GitCommit.PushMetadata(false, "refs/pull/2/merge")
        );
        Mockito.when(githubClient.fetchPrCommits(12345L, "gh-stats-app", "test-repository", 2))
                .thenReturn(Mono.just(List.of(commit)));
        Mockito.when(githubClient.createOrUpdatePrComment(Mockito.eq(12345L), Mockito.eq("gh-stats-app"), Mockito.eq("test-repository"), Mockito.eq(2), Mockito.anyString()))
                .thenReturn(Mono.empty());

        webClient.post()
                .uri("/integrations/github/events")
                .header("X-GitHub-Event", "pull_request")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(openedPrEvent))
                .exchange()
                .expectStatus()
                .isAccepted();

        Mockito.verify(githubClient).createOrUpdatePrComment(
                Mockito.eq(12345L),
                Mockito.eq("gh-stats-app"),
                Mockito.eq("test-repository"),
                Mockito.eq(2),
                Mockito.argThat(comment -> comment.contains("Achievement Pending")
                        && comment.contains("@bgalek")
                        && !comment.contains("windows-language")
                        && !comment.contains("Achievements Unlocked"))
        );
        assertThat(counterValue("ghstats.github.pull_request.processed", "result", "unlocks_commented"))
                .isEqualTo(processedBefore + 1);
        assertThat(counterValue("ghstats.achievements.unlocks", "achievement", "windows-language", "result", "created"))
                .isEqualTo(unlocksBefore);
    }

    @Test
    @DisplayName("should skip PR webhook without installation id")
    void testPullRequestEventWithoutInstallationId() {
        double skippedBefore = counterValue("ghstats.github.webhook.skipped", "reason", "missing_installation");

        webClient.post()
                .uri("/integrations/github/events")
                .header("X-GitHub-Event", "pull_request")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(openedPrEventWithoutInstallation))
                .exchange()
                .expectStatus()
                .isAccepted();

        Mockito.verifyNoInteractions(githubClient);
        assertThat(counterValue("ghstats.github.webhook.skipped", "reason", "missing_installation"))
                .isEqualTo(skippedBefore + 1);
    }

    @Test
    @DisplayName("should skip PR webhook with missing pull request number")
    void testMissingPullRequestNumber() {
        double skippedBefore = counterValue("ghstats.github.webhook.skipped", "reason", "invalid_payload");

        webClient.post()
                .uri("/integrations/github/events")
                .header("X-GitHub-Event", "pull_request")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(missingNumberPrEvent))
                .exchange()
                .expectStatus()
                .isAccepted();

        Mockito.verifyNoInteractions(githubClient);
        assertThat(counterValue("ghstats.github.webhook.skipped", "reason", "invalid_payload"))
                .isEqualTo(skippedBefore + 1);
    }

    private double counterValue(String name, String... tags) {
        Counter counter = meterRegistry.find(name).tags(tags).counter();
        if (counter == null) {
            return 0;
        }
        return counter.count();
    }

    String mergedPrEvent = """
            {
                "action": "closed",
                "number": 1,
                "pull_request": {
                    "merged": true,
                    "html_url": "https://github.com/bgalek/gh-events-test/pull/1"
                },
                "repository": {
                    "name": "gh-events-test",
                    "full_name": "bgalek/gh-events-test",
                    "owner": {
                        "login": "bgalek",
                        "type": "User"
                    }
                },
                "installation": {
                    "id": 12345,
                    "node_id": "MDIzOkludGVncmF0aW9uMQ=="
                },
                "sender": {
                    "login": "bgalek"
                }
            }
            """;

    String closedNotMergedPrEvent = """
            {
                "action": "closed",
                "number": 2,
                "pull_request": {
                    "merged": false
                },
                "repository": {
                    "name": "gh-events-test",
                    "owner": {
                        "login": "bgalek"
                    }
                }
            }
            """;

    String issuesEvent = """
            {
                "action": "opened",
                "issue": {
                    "number": 3
                },
                "repository": {
                    "name": "gh-events-test",
                    "owner": {
                        "login": "bgalek"
                    }
                }
            }
            """;

    String pushEvent = """
            {
                "ref": "refs/heads/main",
                "forced": false,
                "commits": [
                    {
                        "id": "abc123push",
                        "message": "fix: push payload",
                        "timestamp": "2026-08-07T08:48:41Z",
                        "url": "https://github.com/bgalek/gh-events-test/commit/abc123push",
                        "author": {
                            "name": "Bartosz Galek",
                            "email": "bartosz@email.local",
                            "username": "bgalek"
                        },
                        "added": [
                            "push.txt"
                        ],
                        "removed": [],
                        "modified": []
                    }
                ],
                "repository": {
                    "name": "gh-events-test",
                    "owner": {
                        "login": "bgalek"
                    }
                },
                "installation": {
                    "id": 12345
                }
            }
            """;

    String openedPrEvent = """
            {
                "action": "opened",
                "number": 2,
                "pull_request": {
                    "merged": false,
                    "html_url": "https://github.com/gh-stats-app/test-repository/pull/2"
                },
                "repository": {
                    "name": "test-repository",
                    "full_name": "gh-stats-app/test-repository",
                    "owner": {
                        "login": "gh-stats-app",
                        "type": "Organization"
                    }
                },
                "installation": {
                    "id": 12345
                },
                "sender": {
                    "login": "bgalek"
                }
            }
            """;

    String openedPrEventWithoutInstallation = """
            {
                "action": "opened",
                "number": 2,
                "pull_request": {
                    "merged": false,
                    "html_url": "https://github.com/gh-stats-app/test-repository/pull/2"
                },
                "repository": {
                    "name": "test-repository",
                    "full_name": "gh-stats-app/test-repository",
                    "owner": {
                        "login": "gh-stats-app",
                        "type": "Organization"
                    }
                },
                "sender": {
                    "login": "bgalek"
                }
            }
            """;

    String missingNumberPrEvent = """
            {
                "action": "closed",
                "number": null,
                "pull_request": {
                    "merged": true
                },
                "repository": {
                    "name": "gh-events-test",
                    "owner": {
                        "login": "bgalek"
                    }
                },
                "installation": {
                    "id": 12345
                }
            }
            """;

    String pingEvent = """
            {
                "zen": "Mind your words, they are important.",
                "hook_id": 662441247,
                "hook": {
                    "type": "Organization",
                    "id": 662441247,
                    "name": "web",
                    "active": true,
                    "events": [
                        "*"
                    ],
                    "config": {
                        "content_type": "form",
                        "url": "https://api.gh-stats.app/integrations/github/events"
                    }
                },
                "organization": {
                    "login": "gh-stats-app"
                },
                "sender": {
                    "login": "bgalek"
                }
            }
            """;
}
