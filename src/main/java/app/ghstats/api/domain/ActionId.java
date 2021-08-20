package app.ghstats.api.domain;

public record ActionId(Owner user, ActionName name) {

    public static ActionId valueOf(String value) {
        String[] split = value.split("/", 2);
        return new ActionId(Owner.valueOf(split[0]), ActionName.valueOf(split[1]));
    }

    public static ActionId valueOf(Owner user, ActionName repository) {
        return new ActionId(user, repository);
    }

    public static ActionId fromGithubString(String serialized) {
        String[] split = serialized.replaceFirst("__", "").replaceFirst("_", "/").split("/", 2);
        return new ActionId(Owner.valueOf(split[0]), ActionName.valueOf(split[1]));
    }

    public String serialize() {
        return "%s/%s".formatted(user.value(), name.value());
    }

    @Override
    public String toString() {
        return serialize();
    }

    public static record ActionName(String value) {
        public static ActionName valueOf(String name) {
            return new ActionName(name);
        }

        @Override
        public String toString() {
            return value;
        }
    }

    public static record Owner(String value) {
        public static Owner valueOf(String owner) {
            return new Owner(owner);
        }

        @Override
        public String toString() {
            return value;
        }
    }
}
