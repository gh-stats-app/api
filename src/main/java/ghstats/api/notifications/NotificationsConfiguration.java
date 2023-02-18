package ghstats.api.notifications;

import ghstats.api.services.mailgun.MailgunClient;
import ghstats.api.services.slack.SlackClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class NotificationsConfiguration {

    @Bean
    NotificationsCommand notificationsCommand(SlackClient slackClient, MailgunClient mailgunClient) {
        return new NotificationsCommand(slackClient, mailgunClient);
    }
}
