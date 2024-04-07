package faang.school.postservice.kafka.consumers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.kafka.KafkaCommentEvent;
import faang.school.postservice.service.newsfeed.NewsFeedService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaCommentConsumer implements KafkaConsumer {

    private final ObjectMapper objectMapper;
    private final NewsFeedService newsFeedService;

    @Override
    @KafkaListener(topics = "${spring.data.kafka.topics.commentTopic.name}",
            groupId = "${spring.data.kafka.groupId}")
    public void listen(String message) {
        try {
            KafkaCommentEvent event = objectMapper.readValue(message, KafkaCommentEvent.class);
            newsFeedService.addCommentToPost(event.getPostId(), event.getId());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}