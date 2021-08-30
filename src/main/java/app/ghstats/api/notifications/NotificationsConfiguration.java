package app.ghstats.api.notifications;

import app.ghstats.api.services.mailgun.MailgunClient;
import app.ghstats.api.services.slack.SlackClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public
class NotificationsConfiguration {

    @Bean
    NotificationsCommand notificationsCommand(SlackClient slackClient, MailgunClient mailgunClient) {
        return new NotificationsCommand(slackClient, mailgunClient);
    }
}
