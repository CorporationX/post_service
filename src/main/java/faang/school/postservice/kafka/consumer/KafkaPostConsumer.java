package faang.school.postservice.kafka.consumer;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.cache.feed.FeedCacher;
import faang.school.postservice.dto.feed.FeedDto;
import faang.school.postservice.dto.post.PostCreate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.LinkedHashSet;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaPostConsumer {

    private final FeedCacher feedCacher;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "${spring.kafka.topics.post-create.name}", groupId = "1")
    public void onMessage(PostCreate messages) {
        log.info("Received message {}", messages.toString());
        var postSubscribers = objectMapper.convertValue(
                messages,
                new TypeReference<PostCreate>() { });
        var feeds = postSubscribers.getUserIds().stream()
                        .map(userId -> FeedDto.builder()
                                .userId(userId.toString())
                                .postIds(new LinkedHashSet<>(Set.of(postSubscribers.getPostId())))
                                .build())
                        .toList();
        feedCacher.cache(feeds);
    }
}
