package ghstats.api.integrations.github;

import ghstats.api.BaseIntegrationTest;
import ghstats.api.services.github.GithubClient;
import ghstats.api.integrations.github.api.CommitAuthor;
import ghstats.api.integrations.github.api.CommitId;
import ghstats.api.integrations.github.api.GitCommit;
import ghstats.api.integrations.github.api.UserEmail;
import ghstats.api.integrations.github.api.UserName;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.time.ZonedDateTime;
import java.util.List;

class GithubEventsApiTest extends BaseIntegrationTest {

    @MockitoBean
    GithubClient githubClient;

    @Test
    @DisplayName("should process merged PR event")
    void testMergedPrEvent() {
        // given
        var commit = new GitCommit(
                CommitId.valueOf("abc123"),
                new CommitAuthor(UserName.valueOf("bgalek"), UserEmail.valueOf("bartosz@email.local")),
                "fix: something",
                ZonedDateTime.now(),
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
    }

    @Test
    @DisplayName("should skip non-merged PR event")
    void testNonMergedPrEvent() {
        webClient.post()
                .uri("/integrations/github/events")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(closedNotMergedPrEvent))
                .exchange()
                .expectStatus()
                .isAccepted();

        Mockito.verifyNoInteractions(githubClient);
    }

    String mergedPrEvent = """
            {
                "action": "closed",
                "number": 1,
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
}
