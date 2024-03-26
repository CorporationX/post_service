package faang.school.postservice.model.redis;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;

@RedisHash("Users")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class RedisUser implements Serializable {
    @Id
    private long id;
    private String username;
}
