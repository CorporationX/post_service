package faang.school.postservice.service.redis;

import faang.school.postservice.dto.LikeDto;
import faang.school.postservice.service.LikeDataFetcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class RedisPostLikeCacheService extends RedisLikeCacheServiceAbstract {
    private final LikeDataFetcher likeDataFetcher;

    public RedisPostLikeCacheService(RedisTemplate<String, String> redisTemplate, LikeDataFetcher likeDataFetcher) {
        super(redisTemplate);
        this.likeDataFetcher = likeDataFetcher;
    }

    @Override
    public String zsetKey(Long postId) {
        return keyPrefix() + Objects.requireNonNullElse(postId, "null_post_id");
    }

    @Override
    public String cntKey(Long postId) {
        if (postId == null) {
            return keyPrefix() + "cnt:null_post_id";
        }
        return keyPrefix() + "cnt:" + postId;
    }

    @Override
    public long fetchTotalLikesCountFromDb(Long postId) {
        return likeDataFetcher.getLikesCountByPostId(postId);
    }

    @Override
    public Page<LikeDto> fetchLikesPageFromDb(Long postId, Pageable pageable) {
        return likeDataFetcher.getLikesPageByPostId(postId, pageable);
    }

    @Override
    protected String keyPrefix() {
        return "post:likes:";
    }
}
