package faang.school.postservice.service.hashtags;

import faang.school.postservice.dto.hashtag.HashtagRequestDto;
import faang.school.postservice.dto.hashtag.PostResponseDto;
import faang.school.postservice.model.Post;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
@Service
@RequiredArgsConstructor
public class HashtagRedisService {
    private static final String HASHTAG_KEY = "post#";
    private static final String POST_ID_KEY = "postId:";
    private static final String CONTENT_HASH_KEY = "content:";
    private static final String AUTHOR_HASH_KEY = "author:";
    private static final String PROJECT_HASH_KEY = "projectId:";
    private static final String PUBLISHED_DATE_HASH_KEY = "publishedAt:";
    private static final Duration ONE_DAY_TTL = Duration.ofDays(1);
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");

    private final RedisTemplate<String, String> redisTemplate;
    private final ZSetOperations<String, String> zSetOps;
    private final HashOperations<String, String, String> hashOps;
    private final ReentrantLock lock = new ReentrantLock();

    @Value("${app.hashtags.max-cached-posts-per-hashtag}")
    private int maxCachedPosts;

    public void saveHashtag(String tag, Post post) {
        String postIdStr = String.valueOf(post.getId());
        String postIdKey = POST_ID_KEY + post.getId();
        String hashtagKey = HASHTAG_KEY + tag;
        hashOps.put(postIdKey, CONTENT_HASH_KEY, post.getContent());
        if (post.getProjectId() != null) {
            hashOps.put(postIdKey, PROJECT_HASH_KEY, String.valueOf(post.getProjectId()));
        } else {
            hashOps.put(postIdKey, AUTHOR_HASH_KEY, String.valueOf(post.getAuthorId()));
        }
        hashOps.put(postIdKey, PUBLISHED_DATE_HASH_KEY, post.getPublishedAt().format(dateFormatter));
        zSetOps.add(hashtagKey, postIdStr, post.getPublishedAt()
                .atZone(java.time.ZoneId.systemDefault())
                .toEpochSecond());

        redisTemplate.expire(hashtagKey, ONE_DAY_TTL);
        redisTemplate.expire(postIdKey, ONE_DAY_TTL);
        try {
            lock.lock();
            Long size = zSetOps.size(hashtagKey);
            if (size != null && size > maxCachedPosts) {
                Set<String> removedPostsSet = zSetOps.range(hashtagKey, 0, 0);
                if (removedPostsSet != null && !removedPostsSet.isEmpty()) {
                    String removedPostId = removedPostsSet.iterator().next();
                    zSetOps.remove(hashtagKey, removedPostId);
                }
            }
        } finally {
            lock.unlock();
        }
    }

    public Page<PostResponseDto> getPostsByHashtag(HashtagRequestDto hashtagRequestDto) {
        int page = hashtagRequestDto.getPage();
        int size = hashtagRequestDto.getSize();
        String tag = hashtagRequestDto.getTag();
        String hashtagKey = HASHTAG_KEY + tag;
        Long zCardResult = zSetOps.zCard(hashtagKey);
        if (zCardResult == null || page + size - 1 >= zCardResult) {
            return null;
        }
        Set<String> lastPostIds = zSetOps.reverseRange(hashtagKey, page, page + size - 1);
        if (lastPostIds == null) {
            return null;
        }

        List<Object> pipelineResults = redisTemplate.executePipelined((RedisCallback<?>) connection -> {
            List<Object> results = new ArrayList<>();
            for (String postIdStr : lastPostIds) {
                String key = POST_ID_KEY + postIdStr;
                results.add(connection.hashCommands().hGet(
                        key.getBytes(StandardCharsets.UTF_8),
                        CONTENT_HASH_KEY.getBytes(StandardCharsets.UTF_8)
                ));
                results.add(connection.hashCommands().hGet(
                        key.getBytes(StandardCharsets.UTF_8),
                        PROJECT_HASH_KEY.getBytes(StandardCharsets.UTF_8)
                ));
                results.add(connection.hashCommands().hGet(
                        key.getBytes(StandardCharsets.UTF_8),
                        AUTHOR_HASH_KEY.getBytes(StandardCharsets.UTF_8)
                ));
                results.add(connection.hashCommands().hGet(
                        key.getBytes(StandardCharsets.UTF_8),
                        PUBLISHED_DATE_HASH_KEY.getBytes(StandardCharsets.UTF_8)
                ));
            }
            return null;
        });

        int index = 0;
        List<PostResponseDto> result = new ArrayList<>();
        for (String postIdStr : lastPostIds) {
            String content = (String) pipelineResults.get(index++);
            String projectIdStr = (String) pipelineResults.get(index++);
            String authorIdStr = (String) pipelineResults.get(index++);
            String publishedAtStr = (String) pipelineResults.get(index++);

            redisTemplate.expire(hashtagKey, ONE_DAY_TTL);
            redisTemplate.expire(POST_ID_KEY + postIdStr, ONE_DAY_TTL);

            long id = Long.parseLong(postIdStr);
            Long projectId = (projectIdStr != null) ? Long.valueOf(projectIdStr) : null;
            Long authorId = (authorIdStr != null) ? Long.valueOf(authorIdStr) : null;
            result.add(new PostResponseDto(id, content, authorId, projectId, publishedAtStr));
        }
        return new PageImpl<>(result, PageRequest.of(page, size), result.size());
    }
}
