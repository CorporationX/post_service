package faang.school.postservice.consumer.post;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.consumer.MessageConsumer;
import faang.school.postservice.dto.post.PostCreateEvent;
import faang.school.postservice.repository.FeedCacheRepository;
import faang.school.postservice.service.FeedService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.util.concurrent.locks.ReentrantLock;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaPostConsumer implements MessageConsumer<String> {
    private final FeedCacheRepository feedCacheRepository;
    private final FeedService feedService;
    private final ObjectMapper objectMapper;
    private final ReentrantLock lock = new ReentrantLock();

    @Value("${entity.post.max_feed_posts}")
    private int maxFeedPosts;

    @Override
    @KafkaListener(topics = "${spring.data.kafka.topic_names.posts}")
    public void consume(String json, @Header(KafkaHeaders.ACKNOWLEDGMENT) Acknowledgment ack) {
        log.info("Message for published post [{}] received.", json);

        try {
            PostCreateEvent post = objectMapper.readValue(json, PostCreateEvent.class);
            post.followerIds().forEach((followerId) -> feedService.addPostToUser(post.id(), followerId));
            ack.acknowledge();

            log.info("Message for published post [{}] processed successfully.", post.id());
        } catch (JsonProcessingException e) {
            log.info("Error on json parsing for published post [{}].", json, e);
        }
    }
}
