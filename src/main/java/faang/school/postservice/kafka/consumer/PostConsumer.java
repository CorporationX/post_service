package faang.school.postservice.kafka.consumer;

import faang.school.postservice.dto.kafka.PostEvent;
import faang.school.postservice.service.feed.FeedService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class PostConsumer {
    private final FeedService feedService;

    @KafkaListener(topics = "${kafka.topics.posts.topic_name}", groupId = "${kafka.topics.posts.group_id}",
            containerFactory = "postContainerFactory")
    public void consumeEvent(PostEvent postEvent, Acknowledgment ack) throws InterruptedException {
        log.info("Из Kafka получен новый PostEvent c postId: {} и батчом followerIds, начинающимся с followerId: {}",
                postEvent.postId(), postEvent.followerIds().get(0));
        feedService.updateFeeds(postEvent);
        ack.acknowledge();
        log.info("PostEvent из Kafka с postId: {} и батчом followerIds, начинающимся с followerId: {} обработан.",
                postEvent.postId(), postEvent.followerIds().get(0));
    }
}
