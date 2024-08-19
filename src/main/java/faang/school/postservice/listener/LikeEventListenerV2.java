package faang.school.postservice.listener;

import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;

public class LikeEventListenerV2 implements MessageListener {

    @Override
    public void onMessage(Message message, byte[] pattern) {
        System.out.println("Получено сообщение: " + message.toString());
    }
}
