package faang.school.postservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.post.PostResponseDto;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.service.redis.RedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;

@Service
@RequiredArgsConstructor
@Slf4j
public class FeedService {

    private final PostService postService;
    private final UserContext userContext;
    private final PostMapper postMapper;
    private final RedisService redisService;
    private final ObjectMapper mapper;

    @Value("${default_number_post}")
    private Long defaultNumberPosts;

    private static final String FEED_CACHE = "feed:";

    public List<PostResponseDto> getFeed(Long lastPostId) {
        long userId = userContext.getUserId();
        List<PostResponseDto> feed = new ArrayList<>();

        if (lastPostId == null) {
            lastPostId = Long.MAX_VALUE;
        }

        String key = FEED_CACHE + userId;
        ConcurrentLinkedDeque<Long> feedWithPostId = redisService.getAndDeleteFeed(key);

        for (int i = 0; i < defaultNumberPosts; i++) {
            if (!feedWithPostId.isEmpty()) {
                lastPostId = mapper.convertValue(feedWithPostId.removeFirst(), Long.class);
                PostResponseDto post = postService.getPostDtoById(lastPostId);
                feed.add(post);
                log.info("______________________________________________get post from redis__________________________");
            } else {
                List<PostResponseDto> posts = postService
                        .getPostByFollowerIdWithLimit(userId, lastPostId, defaultNumberPosts - i)
                        .stream()
                        .map(postMapper::toDto)
                        .toList();
                log.info("_________________________________________________get post from bd__________________________");
                feed.addAll(posts);
                break;
            }
        }

        redisService.saveToRedis(key, feedWithPostId);

        return feed;
    }
}
