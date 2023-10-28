package faang.school.postservice.service.kafka.listener;

import faang.school.postservice.dto.client.UserDto;
import faang.school.postservice.service.FeedService;
import faang.school.postservice.service.RedisCacheService;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class KafkaFeedHeaterListener extends KafkaAbstractListener {

    private final FeedService feedService;

    public KafkaFeedHeaterListener(RedisCacheService redisCacheService, FeedService feedService) {
        super(redisCacheService);
        this.feedService = feedService;
    }

    @Override
    @KafkaListener(topics = "${spring.kafka.topics.feed-heater-topic}", groupId = "${spring.kafka.client-id}",
    containerFactory = "kafkaListenerUserContainerFactory")
    public void consume(ConsumerRecord<String, Object> message) {
        UserDto userDto = (UserDto) message.value();
        feedService.heatUserFeed(userDto);
        log.info("Feed is heated for user {}", userDto.getId());
    }


}
