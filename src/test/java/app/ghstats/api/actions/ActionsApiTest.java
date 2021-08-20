package app.ghstats.api.actions;

import app.ghstats.api.domain.ActionId;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.boot.autoconfigure.r2dbc.R2dbcAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.reactive.server.WebTestClient.ResponseSpec;
import org.springframework.web.reactive.function.BodyInserters;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@WebFluxTest(controllers = ActionsController.class)
@Import(value = {ActionsConfiguration.class, R2dbcAutoConfiguration.class, FlywayAutoConfiguration.class})
class ActionsApiTest {

    @Autowired
    private WebTestClient webClient;

    @Autowired
    private ActionsQuery actionsQuery;

    @Autowired
    private Flyway flyway;

    @Autowired
    DatabaseClient databaseClient;

    @BeforeEach
    void initTest() {
        flyway.clean();
        flyway.migrate();
    }

    @Test
    @DisplayName("should be able to mark action")
    void markAction() {
        // given
        Map<String, String> request = Map.of(
                "repository", "bgalek/repository",
                "action", "__allegro-actions_verify-configuration"
        );

        // when
        ResponseSpec response = webClient.post()
                .uri("/actions")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(request))
                .header("x-reporter", "tests")
                .exchange();

        //then
        response.expectStatus().isCreated();
        assertEquals(1L, actionsQuery.getUsageCount(ActionId.valueOf("allegro-actions/verify-configuration")).block());

        Map<String, Object> dbRecord = databaseClient.sql("SELECT * FROM `stats`").fetch().all().blockFirst();
        assertEquals(1, dbRecord.get("ID"));
        assertEquals("bgalek/repository", dbRecord.get("REPOSITORY"));
        assertEquals("allegro-actions/verify-configuration", dbRecord.get("ACTION"));
        assertEquals("tests", dbRecord.get("REPORTER"));
    }
}