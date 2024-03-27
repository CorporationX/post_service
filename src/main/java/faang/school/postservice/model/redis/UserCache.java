package faang.school.postservice.model.redis;

import faang.school.postservice.dto.UserDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@RedisHash("UserCache")
public class UserCache implements Serializable {
    @Id
    private long id;
    private String name;
    @TimeToLive
    @Value("${ttl.cache}")
    private long ttl;

    public UserCache(UserDto user) {
        this.id = user.getId();
        this.name = user.getUsername();
    }
}
