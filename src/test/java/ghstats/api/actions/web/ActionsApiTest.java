package ghstats.api.actions.web;

import ghstats.api.BaseIntegrationTest;
import ghstats.api.actions.ActionsQuery;
import ghstats.api.actions.api.ActionId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ActionsApiTest extends BaseIntegrationTest {

    @Autowired
    private ActionsQuery actionsQuery;

    @Autowired
    private DatabaseClient databaseClient;

    @Test
    @DisplayName("should be able to mark action")
    void markAction() {
        // given
        Map<String, String> request = Map.of(
                "repository", "bgalek/repository",
                "action", "allegro-actions/verify-configuration@v1"
        );

        // when
        WebTestClient.ResponseSpec response = webClient.post()
                .uri("/actions")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(request))
                .header("x-reporter", "tests")
                .exchange();

        //then
        response.expectStatus().isCreated();
        assertEquals(1L, actionsQuery.getUsageCount(ActionId.valueOf("allegro-actions/verify-configuration")).block());
        assertEquals(1L, actionsQuery.getUsageCount(ActionId.valueOf("allegro-actions/verify-configuration"), "v1").block());

        Map<String, Object> dbRecord = databaseClient.sql("SELECT * FROM `stats`").fetch().all().blockFirst();
        assertEquals(1, dbRecord.get("ID"));
        assertEquals("bgalek/repository", dbRecord.get("REPOSITORY"));
        assertEquals("allegro-actions/verify-configuration", dbRecord.get("ACTION"));
        assertEquals("tests", dbRecord.get("REPORTER"));
    }
}
