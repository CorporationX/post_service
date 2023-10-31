package faang.school.postservice.service.kafka.consumers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.kafka.KafkaCommentEvent;
import faang.school.postservice.service.feed.FeedService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaCommentConsumer implements KafkaConsumer {

    private final ObjectMapper objectMapper;
    private final FeedService feedService;

    @Override
    @KafkaListener(topics = "${spring.data.kafka.topics.commentTopic.name}",
            groupId = "${spring.data.kafka.groupId}")
    public void listen(String message) {
        try {
            KafkaCommentEvent event = objectMapper.readValue(message, KafkaCommentEvent.class);
            feedService.addCommentToPost(event.getPostId(), event.getId());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}