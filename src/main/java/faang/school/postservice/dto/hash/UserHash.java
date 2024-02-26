package faang.school.postservice.dto.hash;

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
@AllArgsConstructor
@NoArgsConstructor
@Builder
@RedisHash(value = "user")
public class UserHash {
    @Id
    private Long id;
    private String username;
    private String email;
    private String phone;
    private Long countryId;

    @TimeToLive
    private Long ttl;

    @Version
    private Long version;
}
