package faang.school.postservice.dto.hash;

import faang.school.postservice.dto.user.UserDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Component
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@RedisHash("AuthorHash")
public class AuthorHash implements Serializable {

    @Id
    private long authorId;
    private long eventId;
    private AuthorType authorType;
    private UserDto userDto;

    @Version
    private long version;

    @TimeToLive
    private long timeToLive;
}
