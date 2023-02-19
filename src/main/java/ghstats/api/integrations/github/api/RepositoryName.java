package ghstats.api.integrations.github.api;

public record RepositoryName(String value) {
    public static RepositoryName valueOf(String value) {
        return new RepositoryName(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
