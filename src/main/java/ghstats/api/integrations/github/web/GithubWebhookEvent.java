package ghstats.api.integrations.github.web;

enum GithubWebhookEvent {
    PING("ping"),
    PULL_REQUEST("pull_request"),
    PUSH("push"),
    UNSUPPORTED("");

    final String header;

    GithubWebhookEvent(String header) {
        this.header = header;
    }

    static GithubWebhookEvent fromHeader(String header) {
        if (header == null || header.isBlank()) {
            return PULL_REQUEST;
        }
        for (GithubWebhookEvent event : values()) {
            if (event.header.equals(header)) {
                return event;
            }
        }
        return UNSUPPORTED;
    }
}
