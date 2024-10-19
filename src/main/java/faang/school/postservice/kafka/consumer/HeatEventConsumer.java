package faang.school.postservice.kafka.consumer;

import faang.school.postservice.cache.service.NewsFeedService;
import faang.school.postservice.cache.service.UserRedisService;
import faang.school.postservice.kafka.event.heater.HeaterNewsFeedEvent;
import faang.school.postservice.kafka.event.heater.HeaterPostsEvent;
import faang.school.postservice.kafka.event.heater.HeaterUsersEvent;
import faang.school.postservice.cache.service.NewsFeedHeater;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class HeatEventConsumer {
    private final NewsFeedHeater newsFeedHeater;
    private final NewsFeedService newsFeedService;
    private final UserRedisService userRedisService;

    @Async
    @KafkaListener(topics = "${spring.kafka.topic.heater.users}", groupId = "${spring.kafka.consumer.group-id}")
    public void consume(HeaterUsersEvent event, Acknowledgment ack) {
        log.info("Received {}", event.toString());
        userRedisService.saveAll(event.getUsers());
        ack.acknowledge();
    }

    @Async
    @KafkaListener(topics = "${spring.kafka.topic.heater.news-feeds}", groupId = "${spring.kafka.consumer.group-id}")
    public void consume(HeaterNewsFeedEvent event, Acknowledgment ack) {
        log.info("Received {}", event.toString());
        newsFeedService.saveAllNewsFeeds(event.getNewsFeeds());
        ack.acknowledge();
    }

    @Async
    @KafkaListener(topics = "${spring.kafka.topic.heater.posts}", groupId = "${spring.kafka.consumer.group-id}")
    public void consume(HeaterPostsEvent event, Acknowledgment ack) {
        log.info("Received {}", event.toString());
        newsFeedHeater.saveAllPosts(event.getPostIds());
        ack.acknowledge();
    }
}
