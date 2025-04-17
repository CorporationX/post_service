package faang.school.postservice.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.event.PostViewEvent;
import faang.school.postservice.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaPostViewConsumer {
    private final ObjectMapper objectMapper;
    private final PostService postService;

    @KafkaListener(topics = "${spring.data.kafka.topic.post-views.name}",
            groupId = "${spring.data.kafka.topic.post-views.group}")
    public void listen(String postViewEventString, Acknowledgment ack) {
        try {
            PostViewEvent postViewEvent = objectMapper.readValue(postViewEventString, PostViewEvent.class);
            postService.incrementView(postViewEvent.postId());
            ack.acknowledge();
        } catch (JsonProcessingException e) {
            log.error("Произошла ошибка при десериализации ивента PostEvent", e);
        } catch (Exception e) {
            log.error("Произошла ошибка при увеличении просмотров у поста", e);
        }
    }
}
