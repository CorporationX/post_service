package faang.school.postservice.dto.post;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

@Data
@RequiredArgsConstructor
//@AllArgsConstructor
@RedisHash(value = "PostCash")
public class PostCashDto {
    @Id
    Long id;
    String content;
    Long authorId;
    Long projectId;
    Long likesNumber;

    @Value("${spring.newsfeed.post.ttl}")
    private transient Long ttl;

    @TimeToLive
    public Long getTtl() {
        return ttl;
    }
}
