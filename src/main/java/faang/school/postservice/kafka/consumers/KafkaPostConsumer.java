package faang.school.postservice.kafka.consumers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.kafka.KafkaPostEvent;
import faang.school.postservice.service.newsfeed.NewsFeedService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaPostConsumer implements KafkaConsumer {

    private final ObjectMapper objectMapper;
    private final NewsFeedService newsFeedService;

    @Override
    @KafkaListener(topics = "${spring.data.kafka.topics.postTopic.name}",
            groupId = "${spring.data.kafka.groupId}")
    public void listen(String message) {
        try {
            KafkaPostEvent event = objectMapper.readValue(message, KafkaPostEvent.class);
            newsFeedService.addPostToFeeds(event.getPostId(), event.getFollowersId(), event.getPublishedAt());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}