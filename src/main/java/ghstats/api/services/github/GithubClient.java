package ghstats.api.services.github;

import ghstats.api.integrations.github.api.CommitAuthor;
import ghstats.api.integrations.github.api.CommitId;
import ghstats.api.integrations.github.api.GitCommit;
import ghstats.api.integrations.github.api.PullRequestEvidence;
import ghstats.api.integrations.github.api.PullRequestFile;
import ghstats.api.integrations.github.api.UserEmail;
import ghstats.api.integrations.github.api.UserName;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.List;

public class GithubClient {

    private static final String COMMENT_MARKER = "<!-- gh-stats-achievements -->";

    private final WebClient webClient;
    private final GithubAppAuthenticator authenticator;

    GithubClient(WebClient webClient, GithubAppAuthenticator authenticator) {
        this.webClient = webClient;
        this.authenticator = authenticator;
    }

    public Mono<PullRequestEvidence> fetchPullRequestEvidence(long installationId, String owner, String repo, int prNumber) {
        return authenticator.installationToken(installationId)
                .flatMap(token -> {
                    Mono<List<PrCommitResponse>> commitsMono = webClient.get()
                            .uri("/repos/{owner}/{repo}/pulls/{number}/commits?per_page=100", owner, repo, prNumber)
                            .headers(headers -> headers.setBearerAuth(token))
                            .retrieve()
                            .bodyToMono(new ParameterizedTypeReference<>() {});

                    Mono<List<PullRequestFileResponse>> filesMono = webClient.get()
                            .uri("/repos/{owner}/{repo}/pulls/{number}/files?per_page=100", owner, repo, prNumber)
                            .headers(headers -> headers.setBearerAuth(token))
                            .retrieve()
                            .bodyToMono(new ParameterizedTypeReference<>() {});

                    return Mono.zip(commitsMono, filesMono)
                            .map(tuple -> {
                                List<GitCommit> commits = tuple.getT1().stream()
                                        .map(c -> toGitCommit(c, owner, repo, prNumber))
                                        .toList();
                                List<PullRequestFile> files = tuple.getT2().stream()
                                        .map(GithubClient::toPullRequestFile)
                                        .toList();
                                return new PullRequestEvidence(commits, files);
                            });
                });
    }

    public Mono<Void> createOrUpdatePrComment(long installationId, String owner, String repo, int prNumber, String body) {
        return authenticator.installationToken(installationId)
                .flatMap(token -> webClient.get()
                        .uri("/repos/{owner}/{repo}/issues/{number}/comments?per_page=100", owner, repo, prNumber)
                        .headers(headers -> headers.setBearerAuth(token))
                        .retrieve()
                        .bodyToMono(new ParameterizedTypeReference<List<IssueCommentResponse>>() {})
                        .flatMap(comments -> {
                            var existing = comments.stream()
                                    .filter(c -> c.body() != null && c.body().contains(COMMENT_MARKER))
                                    .findFirst();
                            if (existing.isPresent()) {
                                return webClient.patch()
                                        .uri("/repos/{owner}/{repo}/issues/comments/{id}", owner, repo, existing.get().id())
                                        .headers(headers -> headers.setBearerAuth(token))
                                        .bodyValue(new CommentBody(body))
                                        .retrieve()
                                        .bodyToMono(Void.class);
                            } else {
                                return webClient.post()
                                        .uri("/repos/{owner}/{repo}/issues/{number}/comments", owner, repo, prNumber)
                                        .headers(headers -> headers.setBearerAuth(token))
                                        .bodyValue(new CommentBody(body))
                                        .retrieve()
                                        .bodyToMono(Void.class);
                            }
                        }));
    }

    private static GitCommit toGitCommit(PrCommitResponse c, String owner, String repo, int prNumber) {
        String login = c.author() != null ? c.author().login() : "unknown";
        return new GitCommit(
                CommitId.valueOf(c.sha()),
                new CommitAuthor(UserName.valueOf(login), UserEmail.valueOf("")),
                c.commit().message(),
                c.commit().author().date(),
                c.parents() == null ? List.of() : c.parents().stream().map(parent -> CommitId.valueOf(parent.sha())).toList(),
                URI.create(c.htmlUrl() != null ? c.htmlUrl() : "https://github.com/%s/%s/pull/%d".formatted(owner, repo, prNumber)),
                new GitCommit.PushMetadata(false, "refs/pull/%d/merge".formatted(prNumber))
        );
    }

    private static PullRequestFile toPullRequestFile(PullRequestFileResponse file) {
        return new PullRequestFile(
                file.filename(),
                PullRequestFile.Status.fromGithub(file.status()),
                file.additions(),
                file.deletions(),
                file.changes(),
                file.previousFilename(),
                file.patch()
        );
    }

    @SuppressWarnings("UnusedVariable")
    private record PullRequestFileResponse(
            String filename,
            String status,
            int additions,
            int deletions,
            int changes,
            @com.fasterxml.jackson.annotation.JsonProperty("previous_filename") String previousFilename,
            String patch
    ) {
    }

    @SuppressWarnings("UnusedVariable")
    private record CommentBody(String body) {
    }
}
