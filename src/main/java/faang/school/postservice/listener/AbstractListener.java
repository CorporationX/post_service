package faang.school.postservice.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;

import java.io.IOException;

@RequiredArgsConstructor
public abstract class AbstractListener<T> implements MessageListener {

    protected final ObjectMapper objectMapper;

    protected abstract void handleEvent(Message message) throws IOException;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            handleEvent(message);
        } catch (IOException e) {
            throw new RuntimeException(e + "couldn't deserialize message");
        }
    }

}
