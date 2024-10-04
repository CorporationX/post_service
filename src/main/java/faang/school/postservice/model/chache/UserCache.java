package faang.school.postservice.model.chache;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;



@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@RedisHash("UserCache")
public class UserCache {
    @Id
    private long id;
    private String name;

    @TimeToLive
    private Long ttl;
}
