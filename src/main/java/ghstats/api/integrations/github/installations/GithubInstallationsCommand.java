package ghstats.api.integrations.github.installations;

import reactor.core.publisher.Mono;

public class GithubInstallationsCommand {

    private final GithubInstallationsRepository repository;

    GithubInstallationsCommand(GithubInstallationsRepository repository) {
        this.repository = repository;
    }

    public Mono<Void> record(GithubInstallation installation) {
        return repository.save(installation);
    }
}
