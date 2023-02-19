package ghstats.api.integrations.github.api;

public record CommitAuthor(UserName userName, UserEmail userEmail) {
    @Override
    public String toString() {
        return "%s (%s)".formatted(userName.value(), userEmail.value());
    }
}
