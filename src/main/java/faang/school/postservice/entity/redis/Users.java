package faang.school.postservice.entity.redis;

import faang.school.postservice.dto.user.UserDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@RedisHash(value = "users")
public class Users implements Serializable {

    @Id
    private long id;
    private UserDto userDto;
    @TimeToLive
    private Long ttlUsers;
//    private String username;
//    private String email;
//    private List<Long> followersId;
}
