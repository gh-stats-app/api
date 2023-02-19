package ghstats.api.integrations.github.api;

public record CommitId(String value) {
    public static CommitId valueOf(String commitId) {
        return new CommitId(commitId);
    }

    @Override
    public String toString() {
        return value;
    }
}
