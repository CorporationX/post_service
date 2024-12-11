package faang.school.postservice.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.kafka.producer.AuthorPostByHeatKafkaProducer;
import faang.school.postservice.model.dto.CommentDto;
import faang.school.postservice.model.dto.redis.cache.PostFields;
import faang.school.postservice.model.dto.redis.cache.RedisCommentDto;
import faang.school.postservice.model.dto.redis.cache.RedisPostDto;
import faang.school.postservice.model.entity.Post;
import faang.school.postservice.model.event.kafka.AuthorPostByHeatKafkaEvent;
import faang.school.postservice.model.event.kafka.PostPublishedKafkaEvent;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.CommentService;
import faang.school.postservice.service.FeedService;
import faang.school.postservice.service.LikeService;
import faang.school.postservice.service.RedisPostService;
import faang.school.postservice.service.RedisTransactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FeedServiceImpl implements FeedService, RedisTransactional {
    private static final String KEY_PREFIX = "newsfeed:user:";
    private static final String POST_KEY_PREFIX = "post:";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    @Value("${redis.feed.size}")
    private int newsFeedSize;

    @Value("${redis.feed.heater.time-range-days}")
    private int timeRangeDays;

    @Value("${redis.feed.heater.batch-size}")
    private int batchSize;

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;
    private final UserContext userContext;
    private final PostRepository postRepository;
    private final RedisPostService redisPostService;
    private final AuthorPostByHeatKafkaProducer authorPostByHeatKafkaProducer;
    private final CommentService commentService;
    private final ExecutorService singleThreadExecutor;
    private final LikeService likeService;

    public FeedServiceImpl(
            @Qualifier("cacheRedisTemplate") RedisTemplate<String, Object> redisTemplate,
            ObjectMapper objectMapper,
            UserContext userContext,
            PostRepository postRepository,
            RedisPostService redisPostService,
            AuthorPostByHeatKafkaProducer authorPostByHeatKafkaProducer,
            CommentService commentService,
            ExecutorService singleThreadExecutor,
            LikeService likeService) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
        this.userContext = userContext;
        this.postRepository = postRepository;
        this.redisPostService = redisPostService;
        this.authorPostByHeatKafkaProducer = authorPostByHeatKafkaProducer;
        this.commentService = commentService;
        this.singleThreadExecutor = singleThreadExecutor;
        this.likeService = likeService;
    }

    @Override
    public RedisTemplate<String, Object> getRedisTemplate() {
        return redisTemplate;
    }

    @Override
    public void addPost(PostPublishedKafkaEvent event) {
        redisTemplate.executePipelined((RedisCallback<Void>) connection -> {
            String member = String.valueOf(event.getPostId());
            double score = toScore(event.getPublishedAt());

            for (Long followerId : event.getFollowerIds()) {
                String key = createKey(followerId);

                redisTemplate.opsForZSet().add(key, member, score);

                Long size = redisTemplate.opsForZSet().size(key);
                if (size != null && size > newsFeedSize) {
                    redisTemplate.opsForZSet().removeRange(key, 0, size - newsFeedSize - 1);
                }
            }
            return null;
        });
    }

    @Override
    public List<RedisPostDto> getNewsFeed(Long userId, int page, int pageSize) {
        if (!userId.equals(userContext.getUserId())) {
            throw new DataValidationException(
                    String.format("User with id = %d tried to feed of user with id = %d",
                            userContext.getUserId(), userId));
        }
        String key = createKey(userId);

        int start = page * pageSize;
        int end = start + pageSize - 1;

        Set<Object> postIds = redisTemplate.opsForZSet().reverseRange(key, start, end);
        if (postIds == null || postIds.isEmpty()) {
            return List.of();
        }

        List<RedisPostDto> posts = new ArrayList<>();
        for (Object postId : postIds) {
            String postKey = POST_KEY_PREFIX + postId;
            Map<Object, Object> postData = redisTemplate.opsForHash().entries(postKey);

            if (!postData.isEmpty()) {
                posts.add(convertMapToRedisPostDto(postData));
            }
        }

        return posts;
    }

    @Override
    public void startHeatingInBackground() {
        CompletableFuture.runAsync(this::startHeating, singleThreadExecutor);
    }

    private void startHeating() {
        LocalDateTime fromDate = LocalDateTime.now().minusDays(timeRangeDays);
        LocalDateTime toDate = LocalDateTime.now();

        Pageable pageable = PageRequest.of(0, batchSize);
        Page<Post> page;

        do {
            page = postRepository.findPostsByDateRange(fromDate, toDate, pageable);
            List<Post> posts = page.getContent();
            List<Long> postIds = posts.stream()
                    .map(Post::getId)
                    .toList();
            List<RedisPostDto> redisPostDtoList = getRedisPostDtoList(posts);
            redisPostService.savePosts(redisPostDtoList);

            Map<Long, List<Long>> top3CommentsAuthorIdsForPostsMap = commentService.getTop3CommentsAuthorIds(postIds);

            posts.forEach(post -> {
                try {
                    List<Long> commentAuthorIds = top3CommentsAuthorIdsForPostsMap.get(post.getId());
                    authorPostByHeatKafkaProducer.sendEvent(
                            new AuthorPostByHeatKafkaEvent(post.getId(), post.getAuthorId(), commentAuthorIds, post.getPublishedAt()));
                } catch (Exception e) {
                    log.error("Failed to process post {}: {}", post.getId(), e.getMessage(), e);
                }
            });
            pageable = pageable.next();
        } while (!page.isLast());
    }

    private List<RedisPostDto> getRedisPostDtoList(List<Post> posts) {
        List<Long> postIds = posts.stream()
                .map(Post::getId)
                .toList();
        Map<Long, List<CommentDto>> top3CommentsForPosts = commentService.getTop3CommentsForPosts(postIds);
        Map<Long, Integer> postIdCommentCountMap = commentService.getPostIdCommentCountMap(postIds);
        Map<Long, Integer> postIdLikeCountMap = likeService.getPostIdLikeCountMap(postIds);
        Map<Long, Integer> postIdViewCountMap = posts.stream()
                .collect(Collectors.toMap(Post::getId, Post::getViewCount));
        return posts.stream()
                .map(post -> RedisPostDto.builder()
                        .postId(post.getId())
                        .authorId(post.getAuthorId())
                        .content(post.getContent())
                        .createdAt(post.getCreatedAt())
                        .commentCount(postIdCommentCountMap.get(post.getId()))
                        .likeCount(postIdLikeCountMap.get(post.getId()))
                        .recentComments(top3CommentsForPosts.get(post.getId()).stream()
                                .map(commentDto -> new RedisCommentDto(commentDto.getAuthorId(), commentDto.getContent()))
                                .toList())
                        .viewCount(postIdViewCountMap.get(post.getId()))
                        .build())
                .toList();
    }

    private RedisPostDto convertMapToRedisPostDto(Map<Object, Object> postData) {
        RedisPostDto postDto = new RedisPostDto();
        postDto.setPostId(Long.valueOf(postData.get(PostFields.POST_ID).toString()));
        postDto.setAuthorId(Long.valueOf(postData.get(PostFields.AUTHOR_ID).toString()));
        postDto.setContent((String) postData.get(PostFields.CONTENT));
        postDto.setCreatedAt(LocalDateTime.parse((String) postData.get(PostFields.CREATED_AT), formatter));
        postDto.setCommentCount(Integer.parseInt(postData.get(PostFields.COMMENT_COUNT).toString()));
        postDto.setLikeCount(Integer.parseInt(postData.get(PostFields.LIKE_COUNT).toString()));
        postDto.setRecentComments(getComments(postData));
        postDto.setViewCount(Integer.parseInt(postData.get(PostFields.VIEW_COUNT).toString()));
        return postDto;
    }

    private List<RedisCommentDto> getComments(Map<Object, Object> postMap) {
        Object commentsObj = postMap.get(PostFields.RECENT_COMMENTS);
        if (commentsObj == null) {
            return new ArrayList<>();
        }
        try {
            return objectMapper.readValue(commentsObj.toString(), new TypeReference<List<RedisCommentDto>>() {
            });
        } catch (JsonProcessingException e) {
            log.error("Failed to deserialize comments", e);
            return new ArrayList<>();
        }
    }

    private String createKey(Long userId) {
        return KEY_PREFIX + userId;
    }

    private double toScore(LocalDateTime publishedAt) {
        return publishedAt.toInstant(ZoneOffset.UTC).toEpochMilli();
    }
}
