package faang.school.postservice.consumer.post;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.consumer.MessageConsumer;
import faang.school.postservice.dto.post.PostViewEvent;
import faang.school.postservice.service.PostCacheService;
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
    private final PostCacheService postCacheService;
    private final ObjectMapper objectMapper;

    @Override
    @KafkaListener(topics = "${spring.data.kafka.topic_names.post_view}")
    public void consume(String event, @Header(KafkaHeaders.ACKNOWLEDGMENT) Acknowledgment ack) {
        log.info("Message for new post [{}] view received.", event);

        try {
            PostViewEvent post = objectMapper.readValue(event, PostViewEvent.class);
            postCacheService.addView(post.postId());
            ack.acknowledge();

            log.info("Message for new post [{}] view for user [{}] processed successfully.", post.postId(), post.viewerId());
        } catch (JsonProcessingException e) {
            log.info("Error on json parsing for published view post [{}].", event, e);
        }
    }
}
