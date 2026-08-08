package ghstats.api.achievements.api;

import ghstats.api.integrations.github.api.GitCommit;
import ghstats.api.integrations.github.api.GithubUser;
import ghstats.api.integrations.github.api.PullRequestFile;
import ghstats.api.integrations.github.api.PullRequestFile.Status;

import java.util.List;

public record PullRequestContext(
        GithubUser recipient,
        PullRequestSnapshot pullRequest,
        List<GitCommit> commits,
        List<PullRequestFile> files
) {
    public PullRequestContext {
        commits = List.copyOf(commits);
        files = List.copyOf(files);
    }

    public GitCommit triggerCommit() {
        return commits.stream()
                .filter(commit -> commit.id().equals(pullRequest.headSha()))
                .findFirst()
                .orElseGet(commits::getLast);
    }

    public List<String> addedFiles() {
        return filenamesWithStatus(Status.ADDED);
    }

    public List<String> removedFiles() {
        return filenamesWithStatus(Status.REMOVED);
    }

    public List<String> modifiedFiles() {
        return files.stream()
                .filter(file -> file.status() != Status.ADDED && file.status() != Status.REMOVED)
                .map(PullRequestFile::filename)
                .toList();
    }

    private List<String> filenamesWithStatus(Status status) {
        return files.stream()
                .filter(file -> file.status() == status)
                .map(PullRequestFile::filename)
                .toList();
    }
}
