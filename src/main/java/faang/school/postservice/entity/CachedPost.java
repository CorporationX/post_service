package faang.school.postservice.entity;

import jakarta.annotation.Nullable;
import jakarta.persistence.Id;
import jakarta.persistence.Transient;
import lombok.Builder;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;
import org.springframework.data.redis.core.index.Indexed;

import java.time.Instant;

@RedisHash(value = "posts")
@Data
@Builder
public class CachedPost {

    @Id
    private Long id;
    private String content;
    @Indexed
    private Long authorId;

    @Nullable
    @Indexed
    private Long projectId;
    private Instant publishedAt;

}
