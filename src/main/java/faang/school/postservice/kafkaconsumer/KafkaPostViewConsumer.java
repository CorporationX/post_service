package faang.school.postservice.kafkaconsumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.kafka.PostViewEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaPostViewConsumer implements MessageConsumer<String> {
    private final PostCacheService kafkaPostServiceInCache;
    private final ObjectMapper objectMapper;

    @Override
    @KafkaListener(topics = "${spring.data.kafka.topic_names.post_view}")
    public void consume(String event, @Header(KafkaHeaders.ACKNOWLEDGMENT) Acknowledgment acknowledgment) {
        log.info("Post [{}] has been received.", event);

        try {
            PostViewEvent eventPost = objectMapper.readValue(event, PostViewEvent.class);
            kafkaPostServiceInCache.addView(eventPost.postId());
            acknowledgment.acknowledge();

            log.info("Post [{}], created by user [{}] viewed by [{}] has been processed successfully.",
                    eventPost.postId(),
                    eventPost.authorId(),
                    eventPost.viewerId());
        } catch (JsonProcessingException e) {
            log.info("Error of json parsing [{}].", event, e);
        }
    }
}
