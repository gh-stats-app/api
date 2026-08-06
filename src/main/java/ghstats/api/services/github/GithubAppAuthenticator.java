package ghstats.api.services.github;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.net.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.Clock;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

class GithubAppAuthenticator {

    private static final Base64.Encoder BASE64_URL_ENCODER = Base64.getUrlEncoder().withoutPadding();
    private static final String JWT_HEADER = base64Url("{\"alg\":\"RS256\",\"typ\":\"JWT\"}");

    private final WebClient webClient;
    private final long appId;
    private final String privateKeyPem;
    private final Clock clock;
    private final Map<Long, CachedInstallationToken> installationTokens = new ConcurrentHashMap<>();

    GithubAppAuthenticator(WebClient webClient, long appId, String privateKeyPem) {
        this(webClient, appId, privateKeyPem, Clock.systemUTC());
    }

    GithubAppAuthenticator(WebClient webClient, long appId, String privateKeyPem, Clock clock) {
        this.webClient = webClient;
        this.appId = appId;
        this.privateKeyPem = privateKeyPem;
        this.clock = clock;
    }

    Mono<String> installationToken(long installationId) {
        return Mono.defer(() -> {
            CachedInstallationToken cached = installationTokens.get(installationId);
            if (cached != null && cached.isValid(clock.instant())) {
                return Mono.just(cached.token());
            }

            return webClient.post()
                    .uri("/app/installations/{installationId}/access_tokens", installationId)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer %s".formatted(createJwt()))
                    .retrieve()
                    .bodyToMono(InstallationTokenResponse.class)
                    .map(response -> {
                        installationTokens.put(installationId, new CachedInstallationToken(response.token(), response.expiresAt()));
                        return response.token();
                    });
        });
    }

    private String createJwt() {
        Instant now = clock.instant();
        String payload = "{\"iat\":%d,\"exp\":%d,\"iss\":\"%d\"}"
                .formatted(now.minusSeconds(60).getEpochSecond(), now.plusSeconds(9 * 60).getEpochSecond(), appId);
        String signingInput = JWT_HEADER + "." + base64Url(payload);

        try {
            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initSign(readPrivateKey());
            signature.update(signingInput.getBytes(StandardCharsets.UTF_8));
            return signingInput + "." + BASE64_URL_ENCODER.encodeToString(signature.sign());
        } catch (Exception e) {
            throw new IllegalStateException("Could not create GitHub App JWT. Check github.app.private-key.", e);
        }
    }

    private PrivateKey readPrivateKey() throws Exception {
        if (privateKeyPem == null || privateKeyPem.isBlank()) {
            throw new IllegalStateException("github.app.private-key must be configured to call the GitHub API.");
        }

        String pem = privateKeyPem.replace("\\n", "\n").trim();
        String body = pem
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replace("-----BEGIN RSA PRIVATE KEY-----", "")
                .replace("-----END RSA PRIVATE KEY-----", "")
                .replaceAll("\\s", "");

        byte[] der = Base64.getDecoder().decode(body);
        if (pem.contains("BEGIN RSA PRIVATE KEY")) {
            der = pkcs1ToPkcs8(der);
        }

        return KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(der));
    }

    private static byte[] pkcs1ToPkcs8(byte[] pkcs1) {
        byte[] algorithmIdentifier = Base64.getDecoder().decode("MA0GCSqGSIb3DQEBAQUA");
        byte[] version = new byte[]{0x02, 0x01, 0x00};
        byte[] privateKey = derOctetString(pkcs1);
        return derSequence(version, algorithmIdentifier, privateKey);
    }

    private static byte[] derSequence(byte[]... values) {
        return derEncoded((byte) 0x30, values);
    }

    private static byte[] derOctetString(byte[] value) {
        return derEncoded((byte) 0x04, value);
    }

    private static byte[] derEncoded(byte tag, byte[]... values) {
        int length = 0;
        for (byte[] value : values) {
            length += value.length;
        }

        byte[] lengthBytes = derLength(length);
        byte[] result = new byte[1 + lengthBytes.length + length];
        result[0] = tag;
        System.arraycopy(lengthBytes, 0, result, 1, lengthBytes.length);

        int offset = 1 + lengthBytes.length;
        for (byte[] value : values) {
            System.arraycopy(value, 0, result, offset, value.length);
            offset += value.length;
        }
        return result;
    }

    private static byte[] derLength(int length) {
        if (length < 128) {
            return new byte[]{(byte) length};
        }

        int bytes = 0;
        int value = length;
        while (value > 0) {
            bytes++;
            value >>= 8;
        }

        byte[] result = new byte[1 + bytes];
        result[0] = (byte) (0x80 | bytes);
        for (int i = bytes; i > 0; i--) {
            result[i] = (byte) (length & 0xff);
            length >>= 8;
        }
        return result;
    }

    private static String base64Url(String value) {
        return BASE64_URL_ENCODER.encodeToString(value.getBytes(StandardCharsets.UTF_8));
    }

    private static final class InstallationTokenResponse {
        private final String token;
        private final ZonedDateTime expiresAt;

        @JsonCreator
        private InstallationTokenResponse(
                @JsonProperty("token") String token,
                @JsonProperty("expires_at") ZonedDateTime expiresAt
        ) {
            this.token = token;
            this.expiresAt = expiresAt;
        }

        private String token() {
            return token;
        }

        private ZonedDateTime expiresAt() {
            return expiresAt;
        }
    }

    private static final class CachedInstallationToken {
        private final String token;
        private final ZonedDateTime expiresAt;

        private CachedInstallationToken(String token, ZonedDateTime expiresAt) {
            this.token = token;
            this.expiresAt = expiresAt;
        }

        private String token() {
            return token;
        }

        private boolean isValid(Instant now) {
            return expiresAt.toInstant().minusSeconds(60).isAfter(now);
        }
    }
}
