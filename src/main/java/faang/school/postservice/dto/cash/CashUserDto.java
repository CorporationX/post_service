package faang.school.postservice.dto.cash;


import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import java.util.concurrent.TimeUnit;

@RedisHash("users")
public record CashUserDto(
        @Id
        Long id,
        String username,
        @TimeToLive(unit = TimeUnit.DAYS)
        Long ttlDays
) {
}
