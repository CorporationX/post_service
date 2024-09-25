package faang.school.postservice.model.redis;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@RedisHash("User")
public class UserForCache implements Serializable {

    private long id;

    private String username;

    private String email;

    private List<Long> followerIds;

    private List<Long> followeeIds;
}