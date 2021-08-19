package app.ghstats.api.domain;

import com.fasterxml.jackson.annotation.JsonCreator;

public record ActionId(Owner user, ActionName name) {

    @JsonCreator
    public static ActionId valueOf(String serialized) {
        String[] split = serialized.replaceFirst("__", "").replaceFirst("_", "/").split("/", 2);
        return new ActionId(Owner.valueOf(split[0]), ActionName.valueOf(split[1]));
    }

    public static ActionId valueOf(Owner user, ActionName repository) {
        return new ActionId(user, repository);
    }

    public String serialize() {
        return "__%s_%s".formatted(user, name);
    }

    @Override
    public String toString() {
        return "%s/%s".formatted(user, name);
    }

    public static record ActionName(String value) {
        public static ActionName valueOf(String name) {
            return new ActionName(name);
        }
    }

    public static record Owner(String value) {
        public static Owner valueOf(String owner) {
            return new Owner(owner);
        }
    }
}
