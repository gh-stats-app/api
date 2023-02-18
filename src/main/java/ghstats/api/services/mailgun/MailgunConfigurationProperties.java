package ghstats.api.services.mailgun;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "mailgun")
record MailgunConfigurationProperties(String apiUrl, String apiKey) {
}
