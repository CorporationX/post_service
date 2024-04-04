package faang.school.postservice.hash;

import faang.school.postservice.dto.user.UserDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@RedisHash("UserHash")
public class UserHash implements Serializable {

    @Id
    private Long userId;

    private UserDto userDto;

    @TimeToLive
    private Long ttl;

    @Version
    private long version;
}
