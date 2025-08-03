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
@RedisHash
public class CachedAuthor implements Serializable {
    @Id
    private Long id;
    private String username;
    private String email;
}