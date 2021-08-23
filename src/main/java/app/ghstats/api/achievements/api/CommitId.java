package app.ghstats.api.achievements.api;

public record CommitId(String value) {
    public static CommitId valueOf(String commitId) {
        return new CommitId(commitId);
    }
}
