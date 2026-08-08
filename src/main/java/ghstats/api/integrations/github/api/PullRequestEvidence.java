package ghstats.api.integrations.github.api;

import java.util.List;

public record PullRequestEvidence(List<GitCommit> commits, List<PullRequestFile> files) {
    public PullRequestEvidence {
        commits = List.copyOf(commits);
        files = List.copyOf(files);
    }
}
