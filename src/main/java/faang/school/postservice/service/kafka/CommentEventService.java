package faang.school.postservice.service.kafka;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.kafka.event.CommentEventDto;
import faang.school.postservice.model.redis.PostCommentsCache;
import faang.school.postservice.producer.KafkaCommentProducer;
import faang.school.postservice.repository.redis.RedisPostCommentRepository;
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
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentEventService implements KafkaReceivedEventService<CommentEventDto> {
    private final KafkaCommentProducer producer;
    private final KafkaSerializer kafkaSerializer;
    private final RedisPostCommentRepository redisPostCommentRepository;

    @Value("${feed.comment.cache.max-cache-size}")
    private int maxCacheSize;

    @Async("kafkaProducerConsumerExecutor")
    public void produceToBroker(Long postId, CommentDto commentDto) {
        CommentEventDto eventDto = CommentEventDto.builder()
            .postId(postId)
            .commentId(commentDto.id())
            .authorId(commentDto.authorId())
            .build();
        String event = kafkaSerializer.serialize(eventDto);
        producer.send(event);
    }

    @Override
    @Retryable(
        retryFor = {OptimisticLockException.class},
        maxAttemptsExpression = "${feed.comment.retry.max-attempts}",
        backoff = @Backoff(maxDelayExpression = "${feed.comment.retry.delay}")
    )
    public void receiveFromBroker(CommentEventDto event) {
        long postId = event.postId();
        long commentId = event.commentId();

        Optional<PostCommentsCache> cacheOpt = redisPostCommentRepository.findById(postId);
        PostCommentsCache cache = cacheOpt.orElseGet(() ->
            PostCommentsCache.builder()
                .id(postId)
                .commentIds(new LinkedHashSet<>(List.of(commentId)))
                .version(0L)
                .build()
        );

        if (cache.getCommentIds().size() > maxCacheSize) {
            Iterator<Long> iterator = cache.getCommentIds().iterator();
            iterator.next();
            iterator.remove();
        }

        redisPostCommentRepository.save(cache);
    }
}
