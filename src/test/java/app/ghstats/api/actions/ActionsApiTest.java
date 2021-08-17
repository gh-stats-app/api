package app.ghstats.api.actions;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.boot.autoconfigure.r2dbc.R2dbcAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.Objects;

@ExtendWith(SpringExtension.class)
@WebFluxTest(controllers = ActionsController.class)
@Import(value = {ActionsConfiguration.class, R2dbcAutoConfiguration.class, FlywayAutoConfiguration.class})
class ActionsApiTest {

    @Autowired
    private WebTestClient webClient;

    @Test
    @DisplayName("should be able to mark action")
    void markAction() {
        // given
        Map<String, String> request = Map.of(
                "repository", "repositoryName",
                "action", "actionName"
        );

        // expect
        webClient.post()
                .uri("/actions")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(request))
                .exchange()
                .expectStatus()
                .isCreated();
    }

    @Test
    @DisplayName("should be able to mark action using bash")
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