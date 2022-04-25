package ghstats.api.actions.web;

import com.fasterxml.jackson.annotation.JsonProperty;

record MarkActionRequest(
        @JsonProperty("repository") String repository,
        @JsonProperty("action") String action
) {
}
