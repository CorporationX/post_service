package faang.school.postservice.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.client.UserServiceClient;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

@Component
public class AchievementListener extends AbstractEventListener implements MessageListener {

    public AchievementListener(ObjectMapper objectMapper, UserServiceClient userServiceClient,
                               List<NotificationService> notificationServices, List<MessageBuilder<?>> messageBuilders) {
        super(objectMapper, userServiceClient, notificationServices, messageBuilders);
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            var achievement = objectMapper.readValue(message.getBody(), AchievementEvent.class);
            String msg = getMessage(achievement.getClass(), Locale.US);
            addHashtags(achievement.getUserId(), msg);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
