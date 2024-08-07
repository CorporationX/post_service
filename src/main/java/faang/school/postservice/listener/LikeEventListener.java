package faang.school.postservice.listener;

import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

@Component
public class LikeEventListener implements MessageListener {

    @Override
    public void onMessage(Message message, byte[] pattern) {
        System.out.println("Received message: " + message.toString());
    }
}
