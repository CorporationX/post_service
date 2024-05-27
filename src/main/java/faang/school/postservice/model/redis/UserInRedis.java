package faang.school.postservice.model.redis;

import faang.school.postservice.dto.user.UserDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Data
@AllArgsConstructor
@NoArgsConstructor
@RedisHash(value = "UserInRedis", timeToLive = 86400L)
public class UserInRedis {
    @Id
    private long id;
    private UserDto user;
}
