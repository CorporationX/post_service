package faang.school.postservice.service.hash;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.event_broker.PostEvent;
import faang.school.postservice.dto.hash.*;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.mapper.PostPrettyMapper;
import faang.school.postservice.mapper.UserHashMapper;
import faang.school.postservice.mapper.UserPrettyMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.hash.FeedHashRepository;
import faang.school.postservice.repository.hash.PostHashRepository;
import faang.school.postservice.repository.hash.UserHashRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.redis.core.RedisKeyValueTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class FeedHashService {
    private final FeedHashRepository feedHashRepository;
    private final PostHashRepository postHashRepository;
    private final UserHashRepository userHashRepository;
    private final PostRepository postRepository;
    private final RedisKeyValueTemplate redisKVTemplate;
    private final PostPrettyMapper postPrettyMapper;
    private final UserPrettyMapper userPrettyMapper;
    private final UserHashMapper userHashMapper;
    private final UserServiceClient userServiceClient;

    @Value("${feed.size}")
    private int feetSize;

    @Async("taskExecutor")
    @Retryable(retryFor = OptimisticLockingFailureException.class, maxAttemptsExpression = "${feed.retry.maxAttempts}",
            backoff = @Backoff(delayExpression = "${feed.retry.maxDelay}"))
    public void updateFeed(PostEvent postEvent, Acknowledgment acknowledgment) {
        List<Long> followerIds = postEvent.getFollowerIds();

        followerIds.forEach(userId -> {
            FeedHash feedHash = feedHashRepository.findById(userId).orElseGet(() -> new FeedHash(userId, new LinkedHashSet<>()));
            boolean add = feedHash.getPostIds().add(postEvent.getPostId());

            if (add && feedHash.getPostIds().size() > feetSize) {
                Iterator<Long> iterator = feedHash.getPostIds().iterator();
                iterator.next();
                iterator.remove();
            }
            redisKVTemplate.update(feedHash);
        });
        acknowledgment.acknowledge();
    }

    public FeedDto getFeed(Long userId, Long lastPostId) {
        FeedHash feedHash = feedHashRepository.findById(userId)
                .orElseGet(() -> new FeedHash(userId, new LinkedHashSet<>()));
        List<Long> postIds = new ArrayList<>(feedHash.getPostIds());

        Collections.reverse(postIds);

        if (lastPostId != null) {
            int lastIndex = postIds.indexOf(lastPostId);
            if (lastIndex != -1) {
                postIds = postIds.subList(lastIndex + 1, postIds.size());
            } else {
                postIds = Collections.emptyList();
            }
        }

        int toIndex = Math.min(20, postIds.size());
        List<Long> limitedPostIds = postIds.subList(0, toIndex);

        List<PostHash> posts = limitedPostIds.stream()
                .map(postId -> postHashRepository.findById(postId)
                        .orElseGet(() -> {
                            Post post = postRepository.findById(postId).orElseThrow(() -> new EntityNotFoundException("Пост не найден в БД"));
                            return new PostHash(post.getId(), post.getAuthorId(), post.getProjectId(), post.getContent(), post.getPublishedAt(), new LinkedHashSet<>(), new LinkedHashSet<>(), new LinkedHashSet<>());
                        }))
                .toList();

        UserHash userHash = userHashRepository.findById(userId)
                .orElseGet(() -> {
                    UserDto userDto = userServiceClient.getUser(userId);
                    UserHash newUserHash = userHashMapper.toHash(userDto);
                    return userHashRepository.save(newUserHash);
                });

        UserPretty userPretty = userPrettyMapper.toPretty(userHash);
        List<PostPretty> postPretties = postPrettyMapper.toPretty(posts);
        return new FeedDto(userPretty, postPretties);
    }
}
