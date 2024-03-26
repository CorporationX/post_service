package faang.school.postservice.model.redis;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;

@RedisHash(value = "Users", timeToLive = 10)
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class RedisUser implements Serializable {

    @Value("${newsfeed.users_ttl}")
    private int TTL;

    @Id
    private long id;
    private String username;
}
