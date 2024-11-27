package faang.school.postservice.model;

import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

@Data
@RedisHash
public class CacheableUser {
    @Id
    private long id;

    @Length(max = 64)
    private String username;

    private String profilePicSmallFileId;

    @TimeToLive
    private long timeToLive;
}
