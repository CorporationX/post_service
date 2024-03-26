package faang.school.postservice.model.redis;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.PriorityQueue;

@RedisHash("Posts")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class RedisPost implements Serializable {

    @Value("${newsfeed.posts_ttl}")
    private int TTL;

    @Id
    private long id;
    private int likes;
    private String content;
    private long authorId;
    private long views;
    private PriorityQueue<RedisComment> comments;
    private LocalDateTime publishedAt;
    private LocalDateTime updatedAt;
}
