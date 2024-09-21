package faang.school.postservice.model.post;

import faang.school.postservice.model.Comment;
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
@RedisHash("cachePost")
public class CachePost implements Serializable {
    @Id
    private long id;
    private String content;
    private long countLike;
    private Comment firstComment;

    @TimeToLive
    private long ttl;

    @Version
    private int version;

    public void incrementLike() {
        countLike++;
    }

    public void incrementVersion() {
        version++;
    }
}
