package faang.school.postservice.consumer.post;

import faang.school.postservice.dto.cache.feed.FeedCacheDto;
import faang.school.postservice.event.kafka.post.PostCreatedEvent;
import faang.school.postservice.repository.cache.feed.FeedCacheRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaPostConsumer {

    private final FeedCacheRepository feedCacheRepository;

    @KafkaListener(topics = "${spring.data.kafka.topics.postCreatedTopic.name}",
            groupId = "${spring.data.kafka.consumerConfig.groupId}")
    public void listenPostEvent(PostCreatedEvent event, Acknowledgment acknowledgment) {
        log.info("start listenPostEvent with: {}", event);

        List<Long> subscribersIds = event.getSubscribers();

        for (Long subscriberId : subscribersIds) {
            FeedCacheDto feedCacheDto = feedCacheRepository.findBySubscriberId(subscriberId)
                    .orElse(buildFeedCacheDto(subscriberId));
            log.info("feedCacheDto - {}", feedCacheDto.toString());

            feedCacheRepository.addPostId(feedCacheDto, event.getPostId());

            feedCacheRepository.save(feedCacheDto);
        }
        acknowledgment.acknowledge();
    }

    private FeedCacheDto buildFeedCacheDto(Long subscriberId) {
        return FeedCacheDto.builder()
                .subscriberId(subscriberId)
                .postsIds(new TreeSet<>(Comparator.reverseOrder()))
                .build();
    }
}
