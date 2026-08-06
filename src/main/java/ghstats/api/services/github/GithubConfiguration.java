package ghstats.api.services.github;

import com.google.common.net.HttpHeaders;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
class GithubConfiguration {

    @Bean
    GithubClient githubClient(
            @Value("${github.app.id}") long appId,
            @Value("${github.app.private-key}") String privateKey
    ) {
        WebClient webClient = WebClient
                .builder()
                .baseUrl("https://api.github.com")
                .codecs(clientCodecConfigurer -> clientCodecConfigurer.defaultCodecs().maxInMemorySize(16 * 1024 * 1024))
                .defaultHeader(HttpHeaders.USER_AGENT, "gh-stats.app")
                .defaultHeader(HttpHeaders.ACCEPT, "application/vnd.github+json")
                .defaultHeader("X-GitHub-Api-Version", "2022-11-28")
                .build();
        return new GithubClient(webClient, new GithubAppAuthenticator(webClient, appId, privateKey));
    }
}
