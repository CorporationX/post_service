package faang.school.postservice.dto.user;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import java.util.concurrent.TimeUnit;

@Builder
@Setter
@Getter
@RedisHash("users")
public class CacheUserDto {
        @Id
        private Long id;
        private String username;
        @TimeToLive(unit = TimeUnit.DAYS)
        private Long ttlDays;
}