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
import java.util.stream.IntStream;

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
    private int feedSize;

    @Async("taskExecutor")
    @Retryable(retryFor = OptimisticLockingFailureException.class, maxAttemptsExpression = "${feed.retry.maxAttempts}",
            backoff = @Backoff(delayExpression = "${feed.retry.maxDelay}"))
    public void updateFeed(PostEvent postEvent, Acknowledgment acknowledgment) {
        List<Long> followerIds = postEvent.getFollowerIds();

        followerIds.forEach(userId -> {
            FeedHash feedHash = feedHashRepository.findById(userId)
                    .orElseGet(() -> new FeedHash(userId, new TreeSet<>()));

            PostIdTime newPost = new PostIdTime(postEvent.getPostId(), postEvent.getPublishedAt());

            feedHash.getPostIds().add(newPost);

            if (feedHash.getPostIds().size() > feedSize) {
                feedHash.getPostIds().remove(feedHash.getPostIds().last());
            }
            redisKVTemplate.update(feedHash);
        });

        acknowledgment.acknowledge();
    }

    public FeedPretty getFeed(Long userId, Optional<Long> lastPostIdOpt) {
        FeedHash feedHash = feedHashRepository.findById(userId)
                .orElseGet(() -> new FeedHash(userId, new TreeSet<>()));

        List<PostIdTime> sortedPostIdTimes = new ArrayList<>(feedHash.getPostIds());

        int endIndex;
        if (lastPostIdOpt.isPresent()) {
            Long lastPostId = lastPostIdOpt.get();
            endIndex = IntStream.range(0, sortedPostIdTimes.size())
                    .filter(i -> sortedPostIdTimes.get(i).getId() == (lastPostId))
                    .findFirst()
                    .orElse(sortedPostIdTimes.size());
        } else {
            endIndex = Math.min(sortedPostIdTimes.size(), 20);
        }

        int startIndex = Math.max(0, endIndex - 20);

        List<Long> limitedPostIds = sortedPostIdTimes.subList(startIndex, endIndex).stream()
                .map(PostIdTime::getId)
                .toList();

        List<PostPretty> posts = loadPostPretties(limitedPostIds);
        UserPretty userPretty = loadUserPretty(userId);

        return new FeedPretty(userPretty, posts);
    }

    private List<PostPretty> loadPostPretties(List<Long> postIds) {
        List<PostHash> posts = postIds.stream()
                .map(postId -> postHashRepository.findById(postId)
                        .orElseGet(() -> {
                            Post post = postRepository.findById(postId).orElseThrow(() -> new EntityNotFoundException("Пост не найден"));
                            return new PostHash(post.getId(), post.getAuthorId(), post.getProjectId(), post.getContent(), post.getPublishedAt(), new LinkedHashSet<>(), new LinkedHashSet<>(), new LinkedHashSet<>());
                        }))
                .toList();

        return postPrettyMapper.toPretty(posts);
    }

    private UserPretty loadUserPretty(Long userId) {
        UserHash userHash = userHashRepository.findById(userId)
                .orElseGet(() -> {
                    UserDto userDto = userServiceClient.getUser(userId);
                    UserHash newUserHash = userHashMapper.toHash(userDto);
                    return userHashRepository.save(newUserHash);
                });

        return userPrettyMapper.toPretty(userHash);
    }
}
