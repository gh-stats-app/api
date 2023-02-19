package ghstats.api.integrations.github.api;

public record UserEmail(String value) {
    public static UserEmail valueOf(String userEmail) {
        return new UserEmail(userEmail);
    }

    @Override
    public String toString() {
        return value;
    }
}
