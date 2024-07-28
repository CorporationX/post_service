package faang.school.postservice.service.feed;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.feed.FeedDto;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.cache.RedisCacheService;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeedService {
    private final ZSetOperations<String, String> zSetOperations;
    private final UserContext userContext;
    private final RedisCacheService redisCacheService;

    @Setter
    @Value("${spring.data.redis.directory.feed}")
    private String feed;
    @Setter
    @Value("${spring.data.redis.directory.post}")
    private String post;
    @Setter
    @Value("${spring.data.redis.directory.comment}")
    private String comment;
    @Setter
    @Value("${spring.data.redis.directory.infAuthor}")
    private String infAuthor;
    @Setter
    @Value("${value.feedSize}")
    private int feedSize;

    public List<FeedDto> getFeed(String afterPostId) {
        long userId = userContext.getUserId();
        String key = feed + userId;

        double startScore = Double.MAX_VALUE;
        if (afterPostId != null) {
            Double score = zSetOperations.score(key, afterPostId);
            if (score != null) {
                startScore = score - 1;
            }
        }

        Set<String> posts = zSetOperations.reverseRangeByScore(key, Double.MIN_VALUE, startScore, 0, feedSize);

        if (!posts.isEmpty()) {
            return posts.stream().map(idPost -> {
                String postInfo = redisCacheService.getFromHSetCache(post, idPost);
                Long authorId = extractAuthorIdFromJson(postInfo);
                String authorInfo = redisCacheService.getFromHSetCache(infAuthor, authorId.toString());
                Set<String> commentInfo = redisCacheService.getAllZSetValues(comment + idPost);
                Long likeInfo = redisCacheService.getZSetSize(comment + idPost);
                return FeedDto.builder().postInfo(postInfo).authorInfo(authorInfo).commentInfo(commentInfo).likeInfo(likeInfo).build();
            }).toList();
        } else {
            return null;
        }
    }

    protected Long extractAuthorIdFromJson(String json) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(json);
            return jsonNode.has("authorId") ? jsonNode.get("authorId").asLong() : null;
        } catch (Exception e) {
            log.error("Error processing JSON", e);
            return null;
        }
    }
}
