package faang.school.postservice.service.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.service.HashtagService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.Message;

import java.io.IOException;
import java.util.function.Consumer;

@RequiredArgsConstructor
public abstract class AbstractPostListener<T> {
    protected final ObjectMapper objectMapper;
    protected final HashtagService hashtagService;

    public void handleData(Message message, Class<T> type, Consumer<T> consumer) {
        try {
            T event = objectMapper.readValue(message.getBody(), type);
            consumer.accept(event);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
