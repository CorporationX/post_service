package faang.school.postservice.listener;


import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.event.CommentEvent;
import org.springframework.data.redis.connection.Message;
import org.springframework.stereotype.Component;

@Component
public class CommentEventListener extends AbstractListener<CommentEvent> {

    public CommentEventListener(ObjectMapper objectMapper) {
        super(objectMapper);
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {

    }

    @Override
    public void handleEvent(Message message) {

    }
}
