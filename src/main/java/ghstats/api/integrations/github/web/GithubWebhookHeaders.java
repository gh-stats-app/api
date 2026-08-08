package ghstats.api.integrations.github.web;

record GithubWebhookHeaders(
        String event,
        String signature256,
        String deliveryId
) {
}
