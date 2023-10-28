package faang.school.postservice.model.redis;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.redis.core.RedisHash;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@RedisHash(value = "User", timeToLive = 86400L)
public class RedisUser {

    @Id
    private long id;
    private String username;
    private List<Long> followerIds;
    private List<Long> followeeIds;
    private String pictureFileId;
    @Version
    private long version;
}
