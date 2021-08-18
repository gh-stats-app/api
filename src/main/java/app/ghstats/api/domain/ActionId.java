package app.ghstats.api.domain;

import com.fasterxml.jackson.annotation.JsonCreator;

public record ActionId(String value) {
    @JsonCreator
    public static ActionId valueOf(String action) {
        if (action.startsWith("__")) return new ActionId(action.substring(2).replaceFirst("_", "/"));
        return new ActionId(action);
    }
}
