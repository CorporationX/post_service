package faang.school.postservice.config.redis.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;

@Getter
@Setter
@RedisHash(value = "User")
@AllArgsConstructor
public class UserRedis implements Serializable {
    private Long id;
    private String username;
}
