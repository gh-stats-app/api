package ghstats.api.infrastructure;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.web.reactive.config.CorsRegistry;
import org.springframework.web.reactive.config.WebFluxConfigurer;

import java.util.Arrays;

@Configuration
class CorsGlobalConfiguration implements WebFluxConfigurer {

    private final CorsConfigurationProperties corsConfigurationProperties;

    CorsGlobalConfiguration(CorsConfigurationProperties corsConfigurationProperties) {
        this.corsConfigurationProperties = corsConfigurationProperties;
    }

    @Override
    public void addCorsMappings(CorsRegistry corsRegistry) {
        corsRegistry.addMapping("/**")
                .allowedOrigins(corsConfigurationProperties.origins())
                .allowedMethods(Arrays.stream(HttpMethod.values()).map(Enum::name).toArray(String[]::new))
                .maxAge(3600);
    }

    @ConstructorBinding
    @ConfigurationProperties(prefix = "cors")
    record CorsConfigurationProperties(String[] origins) {
    }

}
