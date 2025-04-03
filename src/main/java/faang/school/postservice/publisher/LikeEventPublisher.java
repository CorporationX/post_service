package faang.school.postservice.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import faang.school.postservice.dto.NotificationEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class LikeEventPublisher implements Publisher<NotificationEvent>{
    @Autowired
    private final RedisTemplate<String, Object> redisTemplate;
    @Value("${notification.post.like}")
    private String likeTopic;
    private String LOG_PUBLISHING_MESSAGE = "publishing message {}";


    @Override
    public Class getInstance() {
        return LikeEventPublisher.class;
    }

    @Override
    public void publish(NotificationEvent event, String topic) {
        try {
            ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
            String json = ow.writeValueAsString(event);
            redisTemplate.convertAndSend(likeTopic, json);
            log.info(LOG_PUBLISHING_MESSAGE, json);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
