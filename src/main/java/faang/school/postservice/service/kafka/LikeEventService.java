package faang.school.postservice.service.kafka;

import faang.school.postservice.dto.kafka.event.LikeEventDto;
import faang.school.postservice.model.redis.PostLikesCache;
import faang.school.postservice.producer.KafkaLikeProducer;
import faang.school.postservice.repository.redis.RedisPostLikeRepository;
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

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class LikeEventService implements KafkaReceivedEventService<LikeEventDto> {
    private final KafkaLikeProducer producer;
    private final KafkaSerializer kafkaSerializer;
    private final RedisPostLikeRepository redisPostLikeRepository;

    @Value("${feed.like.cache.max-cache-size}")
    private final int maxCacheSize;

    @Async("kafkaProducerConsumerExecutor")
    public void produce(long postId, long likeId) {
        LikeEventDto eventDto = LikeEventDto.builder()
            .postId(postId)
            .likeId(likeId)
            .build();
        String event = kafkaSerializer.serialize(eventDto);
        producer.send(event);
    }

    @Override
    @Retryable(
        retryFor = {OptimisticLockException.class},
        maxAttemptsExpression = "${feed.like.retry.max-attempts}",
        backoff = @Backoff(maxDelayExpression = "${feed.like.retry.delay}")
    )
    public void receiveFromBroker(LikeEventDto event) {
        Optional<PostLikesCache> cacheOpt = redisPostLikeRepository.findById(event.postId());
        PostLikesCache cache = cacheOpt.orElseGet(() -> PostLikesCache.builder()
                .id(event.postId())
                .likeIds(new LinkedHashSet<>(List.of(event.likeId())))
                .version(0L)
                .build()
        );
        if (cache.getLikeIds().size() > maxCacheSize) {
            Iterator<Long> iterator = cache.getLikeIds().iterator();
            iterator.next();
            iterator.remove();
        }

        redisPostLikeRepository.save(cache);

    }
}
