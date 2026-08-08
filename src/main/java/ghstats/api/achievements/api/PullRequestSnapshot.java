package ghstats.api.achievements.api;

import ghstats.api.integrations.github.api.CommitId;
import ghstats.api.integrations.github.api.OrganisationName;
import ghstats.api.integrations.github.api.RepositoryName;

import java.net.URI;

public record PullRequestSnapshot(
        OrganisationName owner,
        RepositoryName repository,
        int number,
        CommitId headSha,
        URI url
) {
}
