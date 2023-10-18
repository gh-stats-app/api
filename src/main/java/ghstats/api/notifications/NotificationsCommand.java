package ghstats.api.notifications;

import ghstats.api.achievements.api.AchievementUnlocked;
import ghstats.api.services.mailgun.MailgunClient;
import ghstats.api.services.slack.SlackClient;
import reactor.core.publisher.Mono;

public class NotificationsCommand {

    private final SlackClient slackClient;
    @SuppressWarnings("unused")
    private final MailgunClient mailgunClient;

    public NotificationsCommand(SlackClient slackClient, MailgunClient mailgunClient) {
        this.slackClient = slackClient;
        this.mailgunClient = mailgunClient;
    }

    public Mono<Void> notify(AchievementUnlocked achievementUnlocked) {
//        return mailgunClient.sendUnlockedMessage(achievementUnlocked).then(slackClient.sendUnlockedMessage(achievementUnlocked));
        return slackClient.sendUnlockedMessage(achievementUnlocked);
    }
}
