package faang.school.postservice.model.redis;

import faang.school.postservice.model.Like;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.PriorityQueue;

@RedisHash(value = "Posts")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RedisPost implements Serializable {

    @Id
    private long id;
    @Version
    private Long version;
    private String content;
    private Long authorId;
    private long likes;
    private long views;
    private PriorityQueue<RedisComment> comments;
    private LocalDateTime publishedAt;
}
