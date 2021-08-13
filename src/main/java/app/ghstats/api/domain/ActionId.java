package app.ghstats.api.domain;

import com.fasterxml.jackson.annotation.JsonCreator;

public record ActionId(String value) {
    @JsonCreator
    public static ActionId valueOf(String action) {
        return new ActionId(action);
    }
}
