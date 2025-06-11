package faang.school.postservice.service.redis;

import faang.school.postservice.dto.LikeDto;
import faang.school.postservice.service.LikeDataFetcher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@Slf4j
public class RedisCommentLikeCacheService extends RedisLikeCacheServiceAbstract {
    private final LikeDataFetcher likeDataFetcher;

    public RedisCommentLikeCacheService(RedisTemplate<String, String> redisTemplate, LikeDataFetcher likeDataFetcher) {
        super(redisTemplate);
        this.likeDataFetcher = likeDataFetcher;
    }

    @Override
    public String zsetKey(Long commentId) {
        return keyPrefix() + Objects.requireNonNullElse(commentId, "null_comment_id");
    }

    @Override
    public String cntKey(Long commentId) {
        if (commentId == null) {
            return keyPrefix() + "cnt:null_comment_id";
        }
        return keyPrefix() + "cnt:" + commentId;
    }

    @Override
    public long fetchTotalLikesCountFromDb(Long commentId) {
        if (commentId == null) {
            log.info("fetchTotalLikesCountFromDb called with null commentId. Returning 0.");
            throw new IllegalArgumentException("commentId cannot be null.");
        }
        return likeDataFetcher.getLikesCountByCommentId(commentId);
    }

    @Override
    public Page<LikeDto> fetchLikesPageFromDb(Long commentId, Pageable pageable) {
        return likeDataFetcher.getLikesPageByCommentId(commentId, pageable);
    }

    @Override
    protected String keyPrefix() {
        return "comment:likes:";
    }
}
