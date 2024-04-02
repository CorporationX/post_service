package faang.school.postservice.model.redis;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.time.LocalDateTime;

@RedisHash(value = "Comments")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RedisComment implements Serializable {

    @Id
    private long id;
    private String content;
    private long authorId;
    private long likes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
