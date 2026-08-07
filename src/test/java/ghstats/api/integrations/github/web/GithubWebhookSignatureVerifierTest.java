package ghstats.api.integrations.github.web;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class GithubWebhookSignatureVerifierTest {

    @Test
    void shouldAcceptValidSignature() {
        GithubWebhookSignatureVerifier verifier = new GithubWebhookSignatureVerifier("webhook-secret");

        assertThat(verifier.isValid("hello", "sha256=cd6dff2937f02e3b44af32d4243c6fb8a4c24b88650f02e7ef5f3559a9ee9ee5"))
                .isTrue();
    }

    @Test
    void shouldRejectInvalidSignature() {
        GithubWebhookSignatureVerifier verifier = new GithubWebhookSignatureVerifier("webhook-secret");

        assertThat(verifier.isValid("hello", "sha256=invalid"))
                .isFalse();
    }

    @Test
    void shouldAllowRequestsWhenSecretIsNotConfigured() {
        GithubWebhookSignatureVerifier verifier = new GithubWebhookSignatureVerifier("");

        assertThat(verifier.isValid("hello", null))
                .isTrue();
    }
}
