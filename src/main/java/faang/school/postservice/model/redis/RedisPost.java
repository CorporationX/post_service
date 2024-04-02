package faang.school.postservice.model.redis;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Queue;

@RedisHash(value = "Posts")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RedisPost implements Serializable {

    @Id
    private long id;
    private String content;
    private Long authorId;
    private long likes;
    private long views;
    private Queue<RedisComment> comments;
    private LocalDateTime publishedAt;
}
