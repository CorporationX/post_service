package faang.school.postservice.service.kafka.consumers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.kafka.KafkaPostEvent;
import faang.school.postservice.service.feed.FeedService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaPostConsumer implements KafkaConsumer {

    private final ObjectMapper objectMapper;
    private final FeedService feedService;

    @Override
    @KafkaListener(topics = "${spring.data.kafka.topics.postTopic.name}",
            groupId = "${spring.data.kafka.groupId}")
    public void listen(String message) {
        try {
            KafkaPostEvent event = objectMapper.readValue(message, KafkaPostEvent.class);
            feedService.addPostToFeeds(event.getPostId(), event.getFollowersId(), event.getPublishedAt());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}