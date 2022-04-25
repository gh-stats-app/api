package ghstats.api.actions.web;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

@ExtendWith(SpringExtension.class)
@WebFluxTest(controllers = BashController.class)
class BashApiTest {

    @Autowired
    private WebTestClient webClient;

    @Test
    @DisplayName("should be able to get bash reporter code")
    void getBash() {
        // expect
        webClient.get()
                .uri("/actions/bash/v1")
                .exchange()
                .expectStatus()
                .isOk();
    }

    @Test
    @DisplayName("should return bash script shasum")
    void getBashSHASum() {
        // expect
        webClient.get()
                .uri("/actions/bash/v1.sha512")
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(String.class)
                .isEqualTo(fileSha512("bash/1.0/action.sh"));
    }

    private String fileSha512(String path) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-512");
            ClassLoader classLoader = getClass().getClassLoader();
            byte[] bytes = Files.readAllBytes(Path.of(Objects.requireNonNull(classLoader.getResource(path)).getPath()));
            return new BigInteger(1, messageDigest.digest(bytes)).toString(16);
        } catch (NoSuchAlgorithmException | IOException e) {
            throw new IllegalStateException();
        }
    }
}
