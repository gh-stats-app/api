package ghstats.api.integrations.github.web;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;

@Component
class GithubWebhookSignatureVerifier {

    private static final String SIGNATURE_PREFIX = "sha256=";

    private final String secret;

    GithubWebhookSignatureVerifier(@Value("${github.webhook.secret:}") String secret) {
        this.secret = secret;
    }

    boolean isValid(String payload, String signature) {
        if (secret == null || secret.isBlank()) {
            return true;
        }
        if (payload == null || signature == null || !signature.startsWith(SIGNATURE_PREFIX)) {
            return false;
        }

        String expected = SIGNATURE_PREFIX + hmacSha256(payload);
        return MessageDigest.isEqual(
                expected.getBytes(StandardCharsets.US_ASCII),
                signature.getBytes(StandardCharsets.US_ASCII)
        );
    }

    private String hmacSha256(String payload) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            return HexFormat.of().formatHex(mac.doFinal(payload.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new IllegalStateException("Could not verify GitHub webhook signature", e);
        }
    }
}
