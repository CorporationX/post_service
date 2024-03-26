package faang.school.postservice.model.redis;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.time.LocalDateTime;

@RedisHash("Comments")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class RedisComment implements Serializable {
    @Id
    private long id;
    private long postId;
    private long authorId;
    private String content;
    private long likes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
