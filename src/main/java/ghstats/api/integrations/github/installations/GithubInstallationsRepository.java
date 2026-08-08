package ghstats.api.integrations.github.installations;

import reactor.core.publisher.Mono;

interface GithubInstallationsRepository {

    Mono<Void> save(GithubInstallation installation);
}
