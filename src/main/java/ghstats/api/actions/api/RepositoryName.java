package ghstats.api.actions.api;

import com.fasterxml.jackson.annotation.JsonCreator;

public record RepositoryName(String value) {
    @JsonCreator
    public static RepositoryName valueOf(String action) {
        return new RepositoryName(action);
    }
}
