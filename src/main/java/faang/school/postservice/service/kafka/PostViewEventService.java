package faang.school.postservice.service.kafka;

import faang.school.postservice.dto.kafka.event.PostViewEventDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.model.redis.PostViewsCache;
import faang.school.postservice.producer.KafkaPostViewProducer;
import faang.school.postservice.repository.redis.RedisPostViewRepository;
import jakarta.persistence.OptimisticLockException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostViewEventService implements KafkaReceivedEventService<PostViewEventDto> {
    private final KafkaPostViewProducer producer;
    private final KafkaSerializer kafkaSerializer;
    private final RedisPostViewRepository redisPostViewRepository;

    @Value("${feed.post-view.cache.max-cache-size}")
    private final int maxCacheSize;

    @Async("kafkaProducerConsumerExecutor")
    public void produce(Long userId, PostDto postDto) {
        PostViewEventDto eventDto = PostViewEventDto.builder()
            .postId(postDto.id())
            .userId(userId)
            .build();
        String event = kafkaSerializer.serialize(eventDto);
        producer.send(event);
    }

    @Override
    @Retryable(
        retryFor = {OptimisticLockException.class},
        maxAttemptsExpression = "${feed.post-view.retry.max-attempts}",
        backoff = @Backoff(delayExpression = "${feed.post-view.retry.delay}")

    )
    public void receiveFromBroker(PostViewEventDto event) {
        long postId = event.postId();
        Optional<PostViewsCache> cacheOpt = redisPostViewRepository.findById(postId);
        PostViewsCache cache = cacheOpt.orElseGet(()->PostViewsCache.builder()
            .id(postId)
            .userIds(new LinkedHashSet<>())
            .version(0L)
            .build());
        cache.getUserIds().add(event.userId());
        if (cache.getUserIds().size() > maxCacheSize) {
            Iterator<Long> iterator = cache.getUserIds().iterator();
            iterator.next();
            iterator.remove();
        }

        redisPostViewRepository.save(cache);
    }
}
