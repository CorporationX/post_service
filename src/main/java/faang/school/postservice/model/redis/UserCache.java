package faang.school.postservice.model.redis;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@RedisHash(value = "User", timeToLive = 86400L)
public class UserCache implements Serializable {

    @Id
    private long id;
    private String username;
    private String email;
    private String phone;
    private Long countryId;

}