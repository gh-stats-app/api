package ghstats.api.badges.web;

import ghstats.api.BaseIntegrationTest;
import ghstats.api.actions.api.ActionId;
import ghstats.api.badges.BadgesQuery;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import reactor.core.publisher.Mono;

import java.time.Duration;

class BadgesApiTest extends BaseIntegrationTest {

    @MockBean
    private BadgesQuery badgesQuery;

    @Test
    @DisplayName("should return badge in svg format with cache for 60 min")
    void testBadge() {
        // given
        Mockito.when(badgesQuery.getActionsBadge(ActionId.valueOf("bgalek/test-action"), "v1", "brightgreen", new LinkedMultiValueMap<>())).thenReturn(Mono.just("<svg />"));

        // expect
        webClient.get()
                .uri("/badge?action=bgalek/test-action@v1")
                .exchange()
                .expectStatus()
                .isOk()
                .expectHeader()
                .contentType(MediaType.valueOf("image/svg+xml"))
                .expectHeader()
                .cacheControl(CacheControl.maxAge(Duration.ofSeconds(60)).cachePublic());
    }
}
