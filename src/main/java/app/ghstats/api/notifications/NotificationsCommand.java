package app.ghstats.api.notifications;

import app.ghstats.api.achievements.api.AchievementUnlocked;
import app.ghstats.api.services.mailgun.MailgunClient;
import app.ghstats.api.services.slack.SlackClient;
import reactor.core.publisher.Mono;

public class NotificationsCommand {

    private final SlackClient slackClient;
    private final MailgunClient mailgunClient;

    public NotificationsCommand(SlackClient slackClient, MailgunClient mailgunClient) {
        this.slackClient = slackClient;
        this.mailgunClient = mailgunClient;
    }

    public Mono<Void> notify(AchievementUnlocked achievementUnlocked) {
        return mailgunClient.sendUnlockedMessage(achievementUnlocked).then(slackClient.sendUnlockedMessage(achievementUnlocked));
    }
}
