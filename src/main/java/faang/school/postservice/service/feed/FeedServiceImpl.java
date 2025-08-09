package faang.school.postservice.service.feed;

import faang.school.postservice.cache.RedisCache;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.feed.FeedDto;
import faang.school.postservice.dto.kafka.KafkaCommentEventDto;
import faang.school.postservice.dto.kafka.KafkaPostEventDto;
import faang.school.postservice.dto.kafka.KafkaSubscribersFeedHeatDto;
import faang.school.postservice.dto.redis.RedisPostDto;
import faang.school.postservice.kafka.producer.KafkaFeedHeatEventProducer;
import faang.school.postservice.kafka.producer.KafkaSubscribersFeedEventProducer;
import faang.school.postservice.mapper.KafkaPostEventToRedisPostMapper;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.FeedService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeedServiceImpl implements FeedService {

    private final KafkaFeedHeatEventProducer kafkaFeedHeatEventProducer;
    private final UserServiceClient userServiceClient;
    private final RedisCache cache;
    private final PostRepository postRepository;
    private final KafkaPostEventToRedisPostMapper kafkaPostEventToRedisPostMapper;
    private final KafkaSubscribersFeedEventProducer kafkaSubscribersFeedEventProducer;
    private final UserContext userContext;

    @Value("${news-feed.heater.batch-size}")
    private int batchSize;

    // Add retry, timeout, try/catch
    @Override
    public void initializeFeedHeat() {
        int lastBatchSize;
        long startingFromId = 0;
        do {
            // 1) Получаем батч пользователей.
            List<Long> userIds = userServiceClient.getUserIdsByBatch(batchSize, startingFromId);
            startingFromId = userIds.get(userIds.size() - 1);
            lastBatchSize = userIds.size();
            kafkaFeedHeatEventProducer.sendMessage(userIds);
        } while (lastBatchSize == batchSize);
    }

    @Override
    public void gatherFollowersForUsers(List<Long> users) {
        // 2) Распределяем подписчиков пользователя по батчам
        users.forEach(userId -> {
            int lastBatchSize;
            long startingFromId = 0;
            do {
                List<Long> followerIds = userServiceClient.getFollowerIdsByBatch(userId, batchSize, startingFromId);
                if (followerIds.isEmpty()) break;
                startingFromId = followerIds.get(followerIds.size() - 1);
                lastBatchSize = followerIds.size();
                kafkaSubscribersFeedEventProducer.sendMessage(userId, followerIds);
            } while (lastBatchSize == batchSize);
        });
    }

    @Override
    public void fillFollowersFeed(KafkaSubscribersFeedHeatDto dto) {
        // 3) Сохраняем батчу подписчиков - батч постов
        putUserIntoCache(dto.userId());
        cache.putFeedForSubscribers(dto.userId(), dto.followerIds());
    }

    @Override
    public void newPostCreated(KafkaPostEventDto eventDto) {
        RedisPostDto redisPostDto = kafkaPostEventToRedisPostMapper.postEventToRedisPostDto(eventDto);
        cache.putPost(redisPostDto);
        putUserIntoCache(redisPostDto.getAuthorId());

        int lastBatchSize;
        long startingFromId = 0;
        do {
            List<Long> followerIds = userServiceClient.getFollowerIdsByBatch(eventDto.authorId(), batchSize, startingFromId);
            if (followerIds.isEmpty()) break;
            startingFromId = followerIds.get(followerIds.size() - 1);
            lastBatchSize = followerIds.size();
            cache.putFeedForUserBatch(followerIds, redisPostDto.getPostId(), redisPostDto.getCreatedAt());
        } while (lastBatchSize == batchSize);
    }

    @Override
    public void putUserIntoCache(long userId) {
        // Исправить на нормального юзера.
        // Вместо своих дто, дополнить UserDto?
        cache.putUser(userServiceClient.getUserForFeed(userId));
    }

    @Override
    public void putCommentInCache(KafkaCommentEventDto dto) {
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
        // Добавить что-бы при сборке поста подтягивались комменты!!!!!!
        return new FeedDto(posts);
    }
}