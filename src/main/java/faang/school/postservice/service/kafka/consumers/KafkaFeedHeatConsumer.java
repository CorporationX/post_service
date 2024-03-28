package faang.school.postservice.service.kafka.consumers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.kafka.KafkaCommentEvent;
import faang.school.postservice.dto.kafka.KafkaFeedHeatEvent;
import faang.school.postservice.service.feed.FeedHeater;
import faang.school.postservice.service.feed.FeedService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaFeedHeatConsumer implements KafkaConsumer {

    private final ObjectMapper objectMapper;
    private final FeedHeater feedHeater;

    @Override
    @KafkaListener(topics = "${spring.data.kafka.topics.feedHeatTopic.name}",
            groupId = "${spring.data.kafka.groupId}")
    public void listen(String message) {
        try {
            KafkaFeedHeatEvent event = objectMapper.readValue(message, KafkaFeedHeatEvent.class);
            feedHeater.heat();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
