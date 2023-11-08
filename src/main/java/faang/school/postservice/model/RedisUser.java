package faang.school.postservice.model;

import faang.school.postservice.dto.user.UserDto;
import org.springframework.data.annotation.Id;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@RedisHash(value = "Users")
public class RedisUser {

    @Id
    private Long userId;

    private UserDto userDto;

    @Version
    private Long version;
}
