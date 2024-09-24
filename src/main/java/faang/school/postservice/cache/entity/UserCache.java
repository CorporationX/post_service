package faang.school.postservice.cache.entity;

import faang.school.postservice.dto.user.UserDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@RedisHash("UserCache")
public class UserCache {
    @Id
    private Long id;

    private UserDto userDto;

    @TimeToLive
    private Long ttl;
}
