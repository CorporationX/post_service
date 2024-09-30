package faang.school.postservice.cache.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;

@RedisHash("Post")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class CachedPost implements Serializable {

    @Id
    private Long id;
    private Long authorId;
    private Long projectId;
    private long countLike;
    private String content;

    @Version
    private Long version;

    public void incrementLike() {
        countLike++;
    }
    public void incrementVersion() {
        version++;
    }
}
