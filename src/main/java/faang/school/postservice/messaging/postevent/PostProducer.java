package faang.school.postservice.messaging.postevent;

import faang.school.postservice.dto.post.PostCacheDto;
import faang.school.postservice.util.JsonMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
@Slf4j
public class PostProducer {
    @Value("${spring.kafka.topics.post-cache-publication}")
    private String postCachePublicationTopic;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void send(PostCacheDto post) {
        CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(postCachePublicationTopic, post);

        future.whenComplete((result, e) -> {
            if (e == null) {
                log.info("Post event was sent: {}", result);
            } else {
                log.error("Failed to send post event: {}", e.getMessage());
            }
        });
    }
}
