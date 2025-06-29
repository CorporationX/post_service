package faang.school.postservice.entity;

import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.annotation.Transient;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

@RedisHash(value = "authors")
@Data
@Builder
public class CachedAuthor {

    @Id
    @Indexed
    private Long authorId;

    private String username;

    @Transient
    @Value("${spring.redis.ttl.author}")
    private long ttl;

}
