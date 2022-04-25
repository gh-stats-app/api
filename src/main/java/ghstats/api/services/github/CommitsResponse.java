package ghstats.api.services.github;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.ZonedDateTime;
import java.util.List;

record CommitsResponse(@JsonProperty("items") List<CommitWrapper> items, @JsonProperty("total_count") int total) {
    record CommitWrapper(@JsonProperty("commit") Commit commit) {
        record Commit(@JsonProperty("committer") Committer committer) {
            record Committer(@JsonProperty("date") ZonedDateTime date) {
            }
        }
    }
}
