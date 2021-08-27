package app.ghstats.api.achievements.api;

public record UserName(String value) {
    public static UserName valueOf(String userId) {
        return new UserName(userId);
    }

    @Override
    public String toString() {
        return value;
    }
}
