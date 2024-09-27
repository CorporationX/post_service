package faang.school.postservice.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.util.List;


@RedisHash(value = "User")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CachedUserDto implements Serializable {
    private Long id;
    private String username;
    private String email;
    private List<Long> subscriberIds;
}
