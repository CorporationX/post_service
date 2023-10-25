package faang.school.postservice.model.redis;

import faang.school.postservice.dto.redis.PostCashedDto;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.ToString;
import org.springframework.data.redis.core.RedisHash;


@RedisHash(value = "PostInCache", timeToLive = 60 * 60 * 24 * 7)
@Builder
@ToString
public class PostForRedis {
    // ид поста в кэше, и список подписчиков автора поста
    // ключ id поста, значение сам пост
    @Id
    private long id;

    private PostCashedDto post;


}
