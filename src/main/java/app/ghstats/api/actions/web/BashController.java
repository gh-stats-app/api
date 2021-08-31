package app.ghstats.api.actions.web;

import com.google.common.net.HttpHeaders;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.time.Duration;

import static org.springframework.http.HttpHeaders.CACHE_CONTROL;

@RestController
@RequestMapping("/actions/bash")
class BashController {

    private final String bash;

    BashController(ResourceLoader resourceLoader) throws IOException {
        this.bash = new String(resourceLoader.getResource("classpath:/bash/1.0/action.sh").getInputStream().readAllBytes());
    }

    @GetMapping("/v1")
    public Mono<ResponseEntity<String>> bash() {
        return Mono.just(ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, "text/x-shellscript")
                .header(CACHE_CONTROL, CacheControl.maxAge(Duration.ofDays(7)).cachePublic().getHeaderValue())
                .body(bash)
        );
    }

    @GetMapping("/v1.sha512")
    public Mono<ResponseEntity<String>> sha512() {
        return Mono.just(ResponseEntity.ok()
                .header(CACHE_CONTROL, CacheControl.maxAge(Duration.ofDays(7)).cachePublic().getHeaderValue())
                .body("f49e481da693a14913d8794d939aea489358f500ddea660936992d5649af5f27ca0e0309a697107c8a4f8dcbadbdfc3de2528c9f351575668b8a3c9d49742cff"));
    }
}
