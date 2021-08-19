package app.ghstats.api.actions;

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
                .body("f737959a8a0821550bccb155298dd2ed899b3231b62ba795114cd8614915211b9685134fbdf8254f45988d7eff5c744552dd6cd77f331fab56a199394432f58f"));
    }
}
