package faang.school.postservice.service.feed;

import faang.school.postservice.cache.RedisCache;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.feed.FeedDto;
import faang.school.postservice.dto.feed.UserFeedDto;
import faang.school.postservice.dto.kafka.KafkaCommentEventDto;
import faang.school.postservice.dto.kafka.KafkaPostEventDto;
import faang.school.postservice.dto.kafka.KafkaSubscribersFeedHeatDto;
import faang.school.postservice.dto.redis.RedisPostDto;
import faang.school.postservice.kafka.producer.KafkaFeedHeatEventProducer;
import faang.school.postservice.kafka.producer.KafkaSubscribersFeedEventProducer;
import faang.school.postservice.mapper.KafkaPostEventToRedisPostMapper;
import faang.school.postservice.service.FeedService;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeedServiceImpl implements FeedService {

    private final KafkaFeedHeatEventProducer kafkaFeedHeatEventProducer;
    private final UserServiceClient userServiceClient;
    private final RedisCache cache;
    private final KafkaPostEventToRedisPostMapper kafkaPostEventToRedisPostMapper;
    private final KafkaSubscribersFeedEventProducer kafkaSubscribersFeedEventProducer;
    private final UserContext userContext;
    private final CircuitBreakerRegistry circuitBreakerRegistry; // Добавлен для Circuit Breaker

    @Value("${news-feed.heater.batch-size}")
    private int batchSize;

    // Add retry, timeout, try/catch
    @Override
    public void initializeFeedHeat() {
        CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker("userServiceCircuitBreaker");
        int lastBatchSize;
        long startingFromId = 0;
        do {
            long finalStartingFromId = startingFromId;
            Supplier<List<Long>> userIdsSupplier = () -> userServiceClient.getUserIdsByBatch(batchSize, finalStartingFromId);
            List<Long> userIds = Try.ofSupplier(CircuitBreaker.decorateSupplier(circuitBreaker, userIdsSupplier))
                    .recover(throwable -> {
                        log.error("UserServiceClient is down, returning empty list for userIds. Error: {}", throwable.getMessage());
                        return Collections.emptyList();
                    })
                    .get();

            if (userIds.isEmpty()) {
                log.warn("UserServiceClient returned an empty list, stopping feed heat initialization.");
                break;
            }

            startingFromId = userIds.get(userIds.size() - 1);
            lastBatchSize = userIds.size();
            kafkaFeedHeatEventProducer.sendMessage(userIds);
        } while (lastBatchSize == batchSize);
    }

    @Override
    public void gatherFollowersForUsers(List<Long> users) {
        users.forEach(userId -> {
            putUserIntoCache(userId);
            processFollowersInBatches(userId, followerIds -> kafkaSubscribersFeedEventProducer.sendMessage(userId, followerIds));
        });
    }

    @Override
    public void fillFollowersFeed(KafkaSubscribersFeedHeatDto dto) {
        putUserIntoCache(dto.userId());
        cache.putFeedForSubscribers(dto.userId(), dto.followerIds());
    }

    @Override
    public void newPostCreated(KafkaPostEventDto eventDto) {
        RedisPostDto redisPostDto = kafkaPostEventToRedisPostMapper.postEventToRedisPostDto(eventDto);
        cache.putPost(redisPostDto);
        putUserIntoCache(redisPostDto.getAuthorId());

        processFollowersInBatches(eventDto.authorId(), followerIds ->
                cache.putFeedForUserBatch(followerIds, redisPostDto.getPostId(), redisPostDto.getCreatedAt())
        );
    }

    @Override
    public void putUserIntoCache(long userId) {
        CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker("userServiceCircuitBreaker");
        Supplier<UserFeedDto> userSupplier = () -> userServiceClient.getUserForFeed(userId);

        Try.ofSupplier(CircuitBreaker.decorateSupplier(circuitBreaker, userSupplier))
                .onSuccess(cache::putUser)
                .onFailure(throwable -> log.error("Failed to fetch or cache user {}: {}", userId, throwable.getMessage()));
    }

    @Override
    public void putCommentInCache(KafkaCommentEventDto dto) {
        // Нужен ли маппер если сущности идентичны?
        cache.putComment(dto);
    }

    @Override
    public void updatePost(long postId, String event) {
        cache.updatePost(postId, event);
    }

    @Override
    public FeedDto getFeed(Long postId) {
        long userId = userContext.getUserId();
        long startIndex = postId != null ? postId : 0L;
        long toIndex = startIndex == 0L ? 20 : startIndex + 20;
        Set<Long> postIds = cache.getFeed(userId, startIndex, toIndex);
        List<RedisPostDto> posts = cache.getPostsBatch(postIds);
        posts.forEach(post -> post.setComments(cache.getComments(post.getPostId())));
        return new FeedDto(posts);
    }

    private void processFollowersInBatches(long userId, Consumer<List<Long>> action) {
        CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker("userServiceCircuitBreaker");
        long startingFromId = 0;
        List<Long> followerIds;
        do {
            long finalStartingFromId = startingFromId;
            Supplier<List<Long>> followerIdsSupplier = () -> userServiceClient.getFollowerIdsByBatch(userId, batchSize, finalStartingFromId);
            followerIds = Try.ofSupplier(CircuitBreaker.decorateSupplier(circuitBreaker, followerIdsSupplier))
                    .recover(throwable -> {
                        log.error("UserServiceClient is down, skipping followers for user {}. Error: {}", userId, throwable.getMessage());
                        return Collections.emptyList();
                    })
                    .get();

            if (followerIds.isEmpty()) {
                break;
            }

            startingFromId = followerIds.get(followerIds.size() - 1);
            action.accept(followerIds);
        } while (followerIds.size() == batchSize);
    }
}