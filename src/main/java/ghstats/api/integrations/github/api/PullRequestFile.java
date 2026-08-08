package ghstats.api.integrations.github.api;

import java.util.Locale;

public record PullRequestFile(
        String filename,
        Status status,
        int additions,
        int deletions,
        int changes,
        String previousFilename,
        String patch
) {
    public enum Status {
        ADDED,
        REMOVED,
        MODIFIED,
        RENAMED,
        COPIED,
        CHANGED,
        UNCHANGED,
        UNKNOWN;

        public static Status fromGithub(String status) {
            if (status == null) {
                return UNKNOWN;
            }
            try {
                return valueOf(status.toUpperCase(Locale.ROOT));
            } catch (IllegalArgumentException ignored) {
                return UNKNOWN;
            }
        }
    }
}
