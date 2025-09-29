package faang.school.postservice.kafka.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.cache.feed.FeedCache;
import faang.school.postservice.dto.post.PostFeedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class PostConsumer {

    private final ObjectMapper objectMapper;
    private final FeedCache feedCache;

    @KafkaListener(topics = "${feed.kafka.topics.post}")
    public void onPostCreated(String rawMessage) {
        try {
            PostFeedEvent postEvent = objectMapper.readValue(rawMessage, PostFeedEvent.class);

            List<Long> followerIds = postEvent.followerIds();
            if (followerIds == null || followerIds.isEmpty()) {
                return;
            }

            feedCache.addAll(followerIds, postEvent.postId(), postEvent.publishedAt());

            log.info("Post {} added to feeds of {} followers",
                    postEvent.postId(), followerIds.size());

        } catch (Exception e) {
            log.error("Failed to process PostFeedEvent. payload={}", rawMessage, e);
        }
    }
}
