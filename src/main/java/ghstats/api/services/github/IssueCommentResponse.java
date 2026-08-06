package ghstats.api.services.github;

import com.fasterxml.jackson.annotation.JsonProperty;

record IssueCommentResponse(
        @JsonProperty("id") long id,
        @JsonProperty("body") String body
) {
}
