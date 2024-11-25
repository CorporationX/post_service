package faang.school.postservice.model.redis;

import jakarta.persistence.Id;
import jakarta.persistence.Transient;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import java.io.Serializable;

@Data
@RedisHash(value = "Authors")
public class AuthorRedis implements Serializable {
    @Id
    private long id;
    private String username;

    @Transient
    @TimeToLive
    @Value("${cache.author.live-time}")
    private long timeToLeave;

    public AuthorRedis(long id, String username) {
        this.id = id;
        this.username = username;
    }
}