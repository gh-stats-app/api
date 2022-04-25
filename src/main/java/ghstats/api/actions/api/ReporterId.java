package ghstats.api.actions.api;

public record ReporterId(String value) {
    public static ReporterId valueOf(String action) {
        return new ReporterId(action);
    }
}
