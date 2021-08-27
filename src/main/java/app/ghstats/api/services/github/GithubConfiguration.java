package app.ghstats.api.services.github;

import com.google.common.net.HttpHeaders;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class GithubConfiguration {

    @Bean
    GithubClient githubClient() {
        WebClient webClient = WebClient
                .builder()
                .codecs(clientCodecConfigurer -> clientCodecConfigurer.defaultCodecs().maxInMemorySize(16 * 1024 * 1024))
                .defaultHeader(HttpHeaders.USER_AGENT, "gh-stats.app")
                .build();
        return new GithubClient(webClient);
    }
}
