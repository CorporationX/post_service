package faang.school.postservice.broker.producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.feed.FeedHeaterEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class FeedHeaterEventProducer extends KafkaProducerService {

    public FeedHeaterEventProducer(KafkaTemplate<String, FeedHeaterEvent> kafkaTemplate,
                                   ObjectMapper objectMapper,
                                   @Value("${spring.kafka.topic.feed-heater-topic}") String topicName) {
        super(kafkaTemplate, objectMapper, topicName);
    }

    @Async("asyncTaskExecutor")
    public void produceFeedHeaterEventAsync(List<Long> userIds) {
        produceFeedHeaterEvent(userIds);
    }

    public void produceFeedHeaterEvent(List<Long> userIds) {

        FeedHeaterEvent feedHeaterEvent = FeedHeaterEvent.builder().userIds(userIds).build();
        super.sendMessage(feedHeaterEvent);
        log.info("Sending FeedHeaterEvent to message broker. User ids : {}", userIds);
    }
}
