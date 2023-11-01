package faang.school.postservice.model.redis;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@RedisHash(value = "Users", timeToLive = 86400)
public class RedisUser implements Serializable {

    @Id
    private Long userId;
    private String username;
    private String email;
    private String phone;
    private String city;
    private String smallFileId;
    private List<Long> followerIds;
    private List<Long> followeeIds;
    @Version
    private int version;

    public synchronized void incrementUserVersion() {
        version++;
    }
}