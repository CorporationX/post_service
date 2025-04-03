package faang.school.postservice.model.cache;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

@RedisHash("posts")
@Getter
@Setter
@Builder
public class CachePost {
    public static final String POST_PREFIX = "posts:";

    @Id
    private Long id;
    private String content;
    private String authorId;

    @TimeToLive
    private Long timeToLive;

    public static String getLikesFieldName() {
        return "likes";
    }
}
