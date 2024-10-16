package faang.school.postservice.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;

import java.io.IOException;

@Slf4j
public abstract class AbstractListener<T> implements MessageListener {
    protected final ObjectMapper objectMapper;

    public AbstractListener(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            T event = objectMapper.readValue(message.getBody(), getType());
            process(event);
        } catch (IOException e) {
            log.error("IOException occurred while message listener worked", e);
        }
    }

    protected abstract void process(T event);

    protected abstract Class<T> getType();
}
