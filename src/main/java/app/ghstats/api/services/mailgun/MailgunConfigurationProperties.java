package app.ghstats.api.services.mailgun;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@ConstructorBinding
@ConfigurationProperties(prefix = "mailgun")
class MailgunConfigurationProperties {

    private final String apiUrl;
    private final String apiKey;

    MailgunConfigurationProperties(String apiUrl, String apiKey) {
        this.apiUrl = apiUrl;
        this.apiKey = apiKey;
    }

    public String getApiUrl() {
        return apiUrl;
    }

    public String getApiKey() {
        return apiKey;
    }
}
