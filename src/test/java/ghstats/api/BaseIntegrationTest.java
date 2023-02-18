package ghstats.api;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest
@AutoConfigureWebTestClient
public class BaseIntegrationTest {

    @Autowired
    private Flyway flyway;

    @Autowired
    protected WebTestClient webClient;

    @BeforeEach
    void initTest() {
        flyway.clean();
        flyway.migrate();
    }
}
