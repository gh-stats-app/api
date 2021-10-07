package app.ghstats.api.integrations;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/integrations/github")
class GithubIntegrationController {

    private static final Logger logger = LoggerFactory.getLogger(GithubIntegrationController.class);

    @PostMapping("/events")
    ResponseEntity<String> events(@RequestBody String githubEvent) {
        logger.info("github event: " + githubEvent);
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/installed")
    ResponseEntity<String> installed(@RequestBody String githubEvent) {
        logger.info("app installed" + githubEvent);
        return ResponseEntity.accepted().build();
    }
}
