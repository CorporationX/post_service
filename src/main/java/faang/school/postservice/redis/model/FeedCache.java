package faang.school.postservice.redis.model;

import jakarta.persistence.Id;
import lombok.Data;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.support.collections.RedisZSet;

import java.io.Serializable;

@RedisHash(value = "feed", timeToLive = 3600)
@Data
public class FeedCache implements Serializable {
    @Id
    private Long followerId;
    private RedisZSet<Long> postIds; //TODO NOT a Best Practice. will be refactored
}