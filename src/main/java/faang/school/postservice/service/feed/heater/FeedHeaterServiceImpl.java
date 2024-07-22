package faang.school.postservice.service.feed.heater;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.kafka.event.feed_heater.FeedHeaterEvent;
import faang.school.postservice.kafka.producer.feed_heater.FeedHeaterProducer;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.mapper.comment.CommentMapper;
import faang.school.postservice.redis.cache.service.comment.CommentCacheService;
import faang.school.postservice.redis.cache.service.feed.FeedCacheService;
import faang.school.postservice.redis.cache.service.post.PostCacheService;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeedHeaterServiceImpl implements FeedHeaterService {

    @Value("${batches.feed-heater.size}")
    private int usersBatchSize;
    @Value("${spring.data.redis.cache.settings.max-feed-size}")
    private int maxFeedSize;

    private final FeedHeaterProducer feedHeaterProducer;
    private final CommentMapper commentMapper;
    private final CommentCacheService commentCacheService;
    private final CommentRepository commentRepository;
    private final PostMapper postMapper;
    private final PostCacheService postCacheService;
    private final PostRepository postRepository;
    private final FeedCacheService feedCacheService;
    private final UserService userService;
    private final UserContext userContext;

    @Override
    public void heatUp() {

        userContext.setUserId(0L);
        List<UserDto> users = userService.getAllUsers();

        List<Map<Long, List<Long>>> batches = ListUtils.partition(users, usersBatchSize)
                .stream()
                .map(usersBatch -> usersBatch.stream()
                        .collect(Collectors.toMap(UserDto::getId, UserDto::getSubscriberIds)
                ))
                .toList();

        for (Map<Long, List<Long>> batch : batches) {
            feedHeaterProducer.produce(new FeedHeaterEvent(batch));
        }
    }

    @Override
    @Transactional(readOnly = true)
    public void handleBatch(Map<Long, List<Long>> userBatch) {

        for (long userId : userBatch.keySet()) {

            List<Long> subscriberIds = userBatch.get(userId);
            postRepository.findFeedPostsByAuthorId(userId, maxFeedSize).stream()
                    .map(postMapper::toRedisCache)
                    .peek(post  -> commentRepository.findAllByPostId(post.getId()).stream()
                            .map(commentMapper::toRedisCache)
                            .forEach(commentCacheService::save))
                    .forEach(postCache -> {
                        postCacheService.save(postCache);
                        subscriberIds.forEach(id -> feedCacheService.addPostToFeed(postCache, id));
                    });
        }
    }
}
