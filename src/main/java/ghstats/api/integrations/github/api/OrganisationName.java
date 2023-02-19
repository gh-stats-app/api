package ghstats.api.integrations.github.api;

public record OrganisationName(String value) {
    public static OrganisationName valueOf(String organisation) {
        return new OrganisationName(organisation);
    }

    @Override
    public String toString() {
        return value;
    }
}
