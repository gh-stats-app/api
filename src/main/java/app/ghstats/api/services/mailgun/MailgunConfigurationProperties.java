package app.ghstats.api.services.mailgun;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@ConstructorBinding
@ConfigurationProperties(prefix = "mailgun")
record MailgunConfigurationProperties(String apiUrl, String apiKey) {
}
