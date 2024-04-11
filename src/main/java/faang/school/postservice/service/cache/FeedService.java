package faang.school.postservice.service.cache;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.UserDto;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.feed_dto.PostForFeedDto;
import faang.school.postservice.dto.kafka_events.CommentKafkaEvent;
import faang.school.postservice.dto.kafka_events.LikeKafkaEvent;
import faang.school.postservice.dto.kafka_events.PostKafkaEvent;
import faang.school.postservice.exception.PostNotFoundException;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.mapper.news_feed.PostForFeedMapper;
import faang.school.postservice.mapper.news_feed.UserCacheMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.redis.Feed;
import faang.school.postservice.model.redis.PostCache;
import faang.school.postservice.model.redis.UserCache;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.redis.RedisFeedRepository;
import faang.school.postservice.repository.redis.RedisPostRepository;
import faang.school.postservice.repository.redis.RedisUserRepository;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.lang.Nullable;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeedService {
    private final CommentMapper commentMapper;
    private final PostRepository postRepository;
    private final UserCacheMapper userCacheMapper;
    private final UserServiceClient userServiceClient;
    private final PostForFeedMapper postForFeedMapper;
    private final RedisPostRepository redisPostRepository;
    private final RedisUserRepository redisUserRepository;
    private final RedisFeedRepository redisFeedRepository;
    @Value("${feed.batch}")
    private int feedBatch;
    @Value("${feed.posts}")
    private int maxPostsInFeed;

    public void addToFeed(PostKafkaEvent postKafkaEvent, Acknowledgment acknowledgment) {
        Long postId = postKafkaEvent.getPostId();
        postKafkaEvent.getSubscriberIds().forEach(subscriberId -> updateFeedForSubscriber(subscriberId, postId));
        acknowledgment.acknowledge();
    }

    @Retryable(retryFor = FeignException.class, maxAttempts = 5, backoff = @Backoff(delay = 500))
    public TreeSet<PostForFeedDto> getFeed(long userId, @Nullable Long afterId) {
        return redisFeedRepository.findById(userId)
                .map(feed -> getNext20PostIds(feed.getPostsIds(), afterId))
                .map(this::buildListOfPosts)
                .orElse(getPostsFromDbAndSaveToRedis(userId));
    }

    public void addLikeToPost(LikeKafkaEvent likeKafkaEvent, Acknowledgment acknowledgment) {
        redisPostRepository.findById(likeKafkaEvent.getPostId()).ifPresent(postCache -> {
            AtomicLong likeValue = postCache.getLikes();
            likeValue.incrementAndGet();
            postCache.setLikes(likeValue);
            redisPostRepository.save(postCache);
            log.info("Likes of post {} are updated", postCache.getId());
            acknowledgment.acknowledge();
        });
    }

    public void addCommentToPost(CommentKafkaEvent commentEvent, Acknowledgment acknowledgment) {
        redisPostRepository.findById(commentEvent.getPostId()).ifPresent(postCache -> {
            synchronized (postCache) {
                TreeSet<CommentDto> comments = postCache.getComments();
                postCache.getComments().add(commentMapper.toDto(commentEvent));
                if (comments.size() > 3) {
                    comments.remove(comments.first());
                }
                redisPostRepository.save(postCache);
                log.info("Comments of post {} are updated", postCache.getId());
                acknowledgment.acknowledge();
            }
        });
    }

    private void updateFeedForSubscriber(Long subscriberId, Long postId) {
        Feed feed = redisFeedRepository.findById(subscriberId).orElse(new Feed(subscriberId, new LinkedHashSet<>()));
        LinkedHashSet<Long> postsIds = feed.getPostsIds();
        if (postsIds.size() >= maxPostsInFeed) {
            var it = postsIds.iterator();
            if (it.hasNext()) {
                it.next();
                it.remove(); // Delete oldest post
                log.info("Post removed from feed");
            }
        }
        postsIds.add(postId);
        log.info("Post {} added to feed", postId);
        redisFeedRepository.save(feed);
    }

//    public void update(Long subscriberId, Long postId) {
//
//        redisTemplate.execute(new SessionCallback<List<Object>>() {
//            @Retryable(retryFor = {DataAccessException.class, OptimisticLockException.class},
//                    maxAttempts = 5, backoff = @Backoff(delay = 1000))
//            @SuppressWarnings("unchecked")
//            @Override
//            public <K, V> List<Object> execute(RedisOperations<K, V> operetions) throws DataValidationException {
//                operetions.watch((K) ("feed:" + subscriberId));
//                Feed feed = (Feed) operetions.opsForValue().get("feed:" + subscriberId);
//                if (feed == null) {
//                    feed = new Feed(subscriberId, new LinkedHashSet<>());
//                }
//                LinkedHashSet<Long> postIds = feed.getPostsIds();
//                updateFeedForSubscriber(subscriberId,postId);
//                operetions.multi();
//                operetions.opsForValue().set((K)("feed:"+subscriberId),(V)feed);
//                var exec = operetions.exec();
//                if (exec==null){
//                    throw new OptimisticLockException();
//                }
//                return exec;
//            } так не работает,да и другие всякие варианты результата необходимого не дали. Удалил,потом решил пример
//        });   такой хотя бы накидать на случай,если интересно,какие были попытки сделать транзакцию)
//    }


    private TreeSet<PostForFeedDto> getPostsFromDbAndSaveToRedis(Long userId) {
        UserDto userDto = userServiceClient.getUser(userId);
        redisUserRepository.save(new UserCache(userDto));
        List<Long> subscriptionsIds = userServiceClient.getSubscriptionsIdsByUserId(userId);
        List<Post> lastPostsByAuthorIds = postRepository.findLastPostsByAuthorIds(subscriptionsIds, maxPostsInFeed);
        log.info("Last posts by authors ids are received: {}", lastPostsByAuthorIds.size());
        return lastPostsByAuthorIds.stream()
                .peek(post -> {
                    updateFeedForSubscriber(userDto.getId(), post.getId());
                    redisPostRepository.save(new PostCache(post));
                })
                .map(post -> postForFeedMapper.toDto(post, userDto))
                .limit(feedBatch)
                .collect(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(PostForFeedDto::getPublishedAt))));
    }

    private List<Long> getNext20PostIds(LinkedHashSet<Long> postIds, @Nullable Long afterId) {
        if (afterId == null) {
            return postIds.stream().limit(feedBatch).toList();
        }
        Iterator<Long> it = postIds.iterator();
        List<Long> next20Posts = new ArrayList<>();
        boolean isFound = false;
        while (it.hasNext() && next20Posts.size() < feedBatch) {
            Long currentPostId = it.next();
            if (isFound || currentPostId.equals(afterId)) {
                isFound = true;
                next20Posts.add(currentPostId);
            }
        }
        log.info("Next 20 posts ids are received");
        return next20Posts;
    }

    private TreeSet<PostForFeedDto> buildListOfPosts(List<Long> postIds) {
        TreeSet<PostForFeedDto> posts = new TreeSet<>(Comparator.comparing(PostForFeedDto::getPublishedAt));
        for (Long postId : postIds) {
            PostCache post = redisPostRepository.findById(postId).orElse(null);
            if (post != null) {
                redisUserRepository.findById(post.getAuthorId()).ifPresent(user -> {
                    UserDto userDto = userCacheMapper.toDto(user);
                    PostForFeedDto postForFeedDto = postForFeedMapper.toDto(post, userDto);
                    posts.add(postForFeedDto);
                });
            } else {
                Post postDb = postRepository.findById(postId).orElseThrow(() ->
                        new PostNotFoundException("Post with id " + postId + " not found"));
                UserDto user = userServiceClient.getUser(postDb.getAuthorId());
                PostForFeedDto postForFeedDto = postForFeedMapper.toDto(postDb, user);
                posts.add(postForFeedDto);
            }
        }
        log.info("List of posts is built");
        return posts;
    }
}
