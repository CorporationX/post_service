package faang.school.postservice.model.redis;

import faang.school.postservice.dto.user.UserDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@RedisHash(value = "AuthorPostInRedis", timeToLive = 86400L)
public class AuthorPostInRedis implements Serializable {

    @Id
    private long id;
    private UserDto user;
}
