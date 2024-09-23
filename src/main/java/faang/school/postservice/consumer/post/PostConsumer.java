package faang.school.postservice.consumer.post;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.event.post.PostEvent;
import faang.school.postservice.service.feed.FeedService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class PostConsumer {

    private final FeedService feedService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "posts", groupId = "postGroup")
    public void consume(String message) throws JsonProcessingException {
        PostEvent postEvent = objectMapper.readValue(message, PostEvent.class);

        List<Long> subscriberIds = postEvent.getSubscriberIds();
        Long postId = postEvent.getPostId();
        log.info("user IDs of post {} have been received", postId);

        for (Long subscriberId : subscriberIds) {
            feedService.updateFeed(subscriberId, postId);
        }

        // После обработки всех подписчиков, можно делать ack
    }
}