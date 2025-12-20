package faang.school.postservice.config.redis.entity;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;
import org.springframework.data.redis.core.index.Indexed;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@RedisHash(value = "Author")
public class Author implements Serializable {

    @Id
    private String id;

    @Indexed
    private Long authorId;

    private Long modelId;

    @TimeToLive
    private Long ttl;
}