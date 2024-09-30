package faang.school.postservice.consumer.feed;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.event.feed.HeatFeedEvent;
import faang.school.postservice.service.feed.HeatFeedService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class HeatFeedConsumer {

    private final ObjectMapper objectMapper;
    private final HeatFeedService HeatFeedService;

    @KafkaListener(topics = "heat-feed", groupId = "heat-feed-group")
    public void consume(String message, Acknowledgment acknowledgment) throws JsonProcessingException {
        List<HeatFeedEvent> events = objectMapper.readValue(
                message, new TypeReference<List<HeatFeedEvent>>() {}
        );
        HeatFeedService.heatFeedUpdateCache(events);
        acknowledgment.acknowledge();
    }
}
