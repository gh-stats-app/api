package app.ghstats.api.badges;

import app.ghstats.api.actions.ActionsQuery;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.boot.autoconfigure.r2dbc.R2dbcAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.time.Duration;

@ExtendWith(SpringExtension.class)
@WebFluxTest(controllers = BadgesController.class)
@Import(value = {R2dbcAutoConfiguration.class, FlywayAutoConfiguration.class})
class BadgesApiTest {

    @Autowired
    private WebTestClient webClient;

    @MockBean
    ActionsQuery actionsQuery;

    @Test
    @DisplayName("should return badge in svg format with cache for 60 min")
    void testBadge() {
        // given
        Mockito.when(actionsQuery.getUsage("bgalek/test-action")).thenReturn(Mono.just(10L));

        // expect
        webClient.get()
                .uri("/badge?action=bgalek/test-action")
                .exchange()
                .expectStatus()
                .isOk()
                .expectHeader()
                .contentType(MediaType.valueOf("image/svg+xml"))
                .expectHeader()
                .cacheControl(CacheControl.maxAge(Duration.ofSeconds(60)).cachePublic());
    }
}