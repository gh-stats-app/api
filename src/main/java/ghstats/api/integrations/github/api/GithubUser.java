package ghstats.api.integrations.github.api;

public record GithubUser(UserName userName, String type) {

    public boolean isBot() {
        return "Bot".equalsIgnoreCase(type) || userName.value().endsWith("[bot]");
    }
}
