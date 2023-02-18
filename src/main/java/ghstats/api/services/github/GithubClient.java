package ghstats.api.services.github;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class GithubClient {

    private final WebClient webClient;

    GithubClient(WebClient webClient) {
        this.webClient = webClient;
    }

    public Mono<ZonedDateTime> getLastCommitDate(String user) {
        URI uri = UriComponentsBuilder.fromHttpUrl("https://api.github.com/search/commits")
                .queryParam("q", "author:%s".formatted(user))
                .queryParam("sort", "committer-date")
                .queryParam("per_page", "1")
                .build()
                .toUri();
        return webClient.get()
                .uri(uri)
                .header(HttpHeaders.ACCEPT, "application/vnd.github.cloak-preview+json")
                .retrieve()
                .bodyToMono(CommitsResponse.class)
                .map(it -> it.items().stream()
                        .map(CommitsResponse.CommitWrapper::commit)
                        .map(CommitsResponse.CommitWrapper.Commit::committer)
                        .map(CommitsResponse.CommitWrapper.Commit.Committer::date)
                        .findFirst())
                .flatMap(it -> it.map(Mono::just).orElseGet(Mono::empty));
    }

    public Mono<List<String>> getAllUserCommits(String user) {
        AtomicInteger pageCounter = new AtomicInteger(1);
        AtomicInteger itemCounter = new AtomicInteger(0);
        return fetchItems(user, pageCounter.get())
                .expand(response -> {
                    if (response.total() > itemCounter.addAndGet(response.items().size())) {
                        int i = pageCounter.incrementAndGet();
                        return Mono.delay(Duration.ofSeconds(5)).then(fetchItems(user, i));
                    }
                    return Mono.empty();
                }, 1)
                .collectList()
                .map(commitsResponses -> commitsResponses.stream()
                        .flatMap(commitsResponse -> commitsResponse.items().stream())
                        .map(commitWrapper -> commitWrapper.commit().toString())
                        .collect(Collectors.toList())
                );
    }

    private Mono<CommitsResponse> fetchItems(String user, int page) {
        URI uri = UriComponentsBuilder.fromHttpUrl("https://api.github.com/search/commits")
                .queryParam("q", "author:%s".formatted(user))
                .queryParam("sort", "committer-date")
                .queryParam("per_page", "100")
                .queryParam("page", page)
                .build()
                .toUri();
        return webClient.get()
                .uri(uri)
                .header(HttpHeaders.ACCEPT, "application/vnd.github.cloak-preview+json")
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> clientResponse.bodyToMono(String.class).flatMap(error -> Mono.error(new RuntimeException(error))))
                .bodyToMono(CommitsResponse.class);
    }
}
