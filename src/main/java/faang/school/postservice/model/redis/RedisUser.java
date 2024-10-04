package faang.school.postservice.model.redis;

import faang.school.postservice.dto.user.UserInfoDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

@RedisHash(value = "user")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RedisUser implements Serializable {
    @Id
    private Long id;
    private UserInfoDto userInfo;
    @TimeToLive(unit = TimeUnit.DAYS)
    private Long timeToLive;
    @Version
    private int version;
}
