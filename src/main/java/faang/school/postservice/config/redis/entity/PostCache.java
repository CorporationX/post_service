package faang.school.postservice.config.redis.entity;

import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;
import org.springframework.data.redis.core.index.Indexed;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@RedisHash(value = "Post")
public class PostCache implements Serializable {

    @Id
    private String id;

    @Indexed
    @NotNull
    private Long postId;

    @NotNull
    private Long authorId;

    private Long projectId;

    @TimeToLive(unit = TimeUnit.SECONDS)
    private Long ttl;
}
