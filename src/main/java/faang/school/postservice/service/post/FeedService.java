package faang.school.postservice.service.post;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.ThreadPoolConfig;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.feed.FeedDto;
import faang.school.postservice.heater.Heater;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.concurrent.Future;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeedService {
    private final ZSetOperations<String, String> zSetOperations;
    private final UserContext userContext;
    private final RedisCache redisCache;
    private final UserServiceClient userServiceClient;
    private final PostServiceImpl postService;
    private final List<Heater> heaters;
    private final ObjectMapper objectMapper;
    private final CommentService commentService;
    private final LikeService likeService;
    private final ThreadPoolConfig threadPoolConfig;

    @Value("${spring.data.redis.directory.feed}")
    @Setter
    private String feed;
    @Value("${spring.data.redis.directory.post}")
    @Setter
    private String post;
    @Setter
    @Value("${spring.data.redis.directory.comment}")
    private String comment;
    @Setter
    @Value("${spring.data.redis.directory.infoAboutAuthor}")
    private String infoAboutAuthor;
    @Value("${value.feedSize}")
    @Setter
    private int feedSize;

    public List<FeedDto> getFeed(String pivotPostId) {
        long userId = userContext.getUserId();
        String key = feed + userId;
        double startScore = Double.MAX_VALUE;
        if (pivotPostId != null) {
            Double score = zSetOperations.score(key, pivotPostId);
            if (score != null) {
                startScore = score - 1;
            }
        }
        Set<String> posts = zSetOperations.reverseRangeByScore(key, Double.MIN_VALUE, startScore, 0, feedSize);
        if (!posts.isEmpty()) {
            return posts.stream().map(idPost -> {
                String postInfo = redisCache.getFromHSetCache(post, idPost);
                Long authorId = extractAuthorIdFromJson(postInfo);
                String authorInfo = redisCache.getFromHSetCache(infoAboutAuthor, authorId.toString());
                Set<String> commentInfo = redisCache.getAllZSetValues(comment + idPost);
                Long likeInfo = redisCache.getZSetSize(comment + idPost);
                return FeedDto.builder().postInfo(postInfo).authorInfo(authorInfo).commentInfo(commentInfo).likeInfo(likeInfo).build();
            }).toList();
        } else {
            return createOldFeed(userId, (long) startScore);
        }
    }

    public void heatCache() {
        List<Long> ids = userServiceClient.getAllUsersId();
        if (!ids.isEmpty()) {
            ids.forEach(userId -> {
                Future<?> future = threadPoolConfig.heaterPool().submit(() -> {
                    heatUserFeed(userId, null);
                });
            });
        }
    }

    public Long extractAuthorIdFromJson(String json) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(json);
            return jsonNode.has("authorId") ? jsonNode.get("authorId").asLong() : null;
        } catch (Exception e) {
            log.error("Error processing JSON", e);
            return null;
        }
    }

    public void heatUserFeed(Long userId, Long pivotPostId) {
        List<Long> postList = postService.findPostIdsByFolloweeId(userId, pivotPostId);
        postList.forEach(postId -> heaters.forEach(heatFeed -> heatFeed.addInfoToRedis(userId, postId)));
    }

    public List<FeedDto> createOldFeed(Long userId, Long StartPostId) {
        List<Long> postList = postService.findPostIdsByFolloweeId(userId, StartPostId);
        if (!postList.isEmpty()) {
            return postList.stream().map(postId -> {
                try {
                    String postInfo = objectMapper.writeValueAsString(postService.getPost(postId));
                    String authorInfo = objectMapper.writeValueAsString(userServiceClient.getUserByPostId(postId));
                    Set<String> commentInfo = commentService.getTheLastCommentsForNewsFeed(postId);
                    Long likeInfo = likeService.getNumberOfLike(postId);
                    return FeedDto.builder().postInfo(postInfo).authorInfo(authorInfo).commentInfo(commentInfo).likeInfo(likeInfo).build();
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }).toList();
        } else {
            return null;
        }
    }
}