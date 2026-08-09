package ghstats.api.services.github;

import ghstats.api.integrations.github.api.CommitId;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class GithubClientTest {

    @Test
    void shouldFetchEveryCommitAndFilePage() {
        List<ClientRequest> requests = new ArrayList<>();
        ExchangeFunction exchange = request -> {
            requests.add(request);
            return Mono.just(responseFor(request));
        };
        var webClient = WebClient.builder()
                .baseUrl("https://api.github.com")
                .exchangeFunction(exchange)
                .build();
        var authenticator = Mockito.mock(GithubAppAuthenticator.class);
        Mockito.when(authenticator.installationToken(42L)).thenReturn(Mono.just("installation-token"));
        var client = new GithubClient(webClient, authenticator);

        var evidence = client.fetchPullRequestEvidence(42L, "owner", "repository", 7).block();

        assertThat(evidence).isNotNull();
        assertThat(evidence.commits()).extracting(commit -> commit.id().value())
                .containsExactly("commit-1", "commit-2");
        assertThat(evidence.commits().getLast().parents())
                .containsExactly(CommitId.valueOf("parent-1"), CommitId.valueOf("parent-2"));
        assertThat(evidence.files()).extracting(file -> file.filename())
                .containsExactly("first.java", "second.java");
        assertThat(requests).hasSize(4).allSatisfy(request ->
                assertThat(request.headers().getFirst(HttpHeaders.AUTHORIZATION))
                        .isEqualTo("Bearer installation-token")
        );
    }

    private ClientResponse responseFor(ClientRequest request) {
        String path = request.url().getPath();
        boolean secondPage = request.url().getQuery().contains("page=2");
        if (path.endsWith("/commits")) {
            return jsonResponse(
                    secondPage ? commit("commit-2", "parent-1", "parent-2") : commit("commit-1", "parent-1"),
                    secondPage ? null : nextPage(path)
            );
        }
        if (path.endsWith("/files")) {
            return jsonResponse(
                    secondPage ? file("second.java") : file("first.java"),
                    secondPage ? null : nextPage(path)
            );
        }
        throw new AssertionError("Unexpected request path " + path);
    }

    private ClientResponse jsonResponse(String body, String nextPage) {
        ClientResponse.Builder response = ClientResponse.create(HttpStatus.OK)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body("[" + body + "]");
        if (nextPage != null) {
            response.header(HttpHeaders.LINK, "<" + nextPage + ">; rel=\"next\"");
        }
        return response.build();
    }

    private String nextPage(String path) {
        return "https://api.github.com" + path + "?per_page=100&page=2";
    }

    private String commit(String sha, String... parents) {
        String parentJson = java.util.Arrays.stream(parents)
                .map(parent -> "{\"sha\":\"" + parent + "\"}")
                .collect(java.util.stream.Collectors.joining(","));
        return """
                {
                  "sha": "%s",
                  "commit": {
                    "message": "message",
                    "author": {"date": "2026-08-08T10:00:00Z"}
                  },
                  "author": {"login": "developer"},
                  "html_url": "https://github.com/owner/repository/commit/%s",
                  "parents": [%s]
                }
                """.formatted(sha, sha, parentJson);
    }

    private String file(String filename) {
        return """
                {
                  "filename": "%s",
                  "status": "modified",
                  "additions": 1,
                  "deletions": 1,
                  "changes": 2,
                  "patch": "@@ -1,1 +1,1 @@\\n-old\\n+new"
                }
                """.formatted(filename);
    }
}
