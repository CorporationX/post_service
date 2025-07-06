package faang.school.postservice.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
@RedisHash("UserCash")
public class UserCashDto {
    Long id;
    String username;
    String email;

    @Value("${spring.newsfeed.user.ttl}")
    private transient Long ttl;

    @TimeToLive
    public Long getTtl() {
        return ttl;
    }
}
