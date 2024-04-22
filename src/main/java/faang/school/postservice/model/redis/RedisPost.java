package faang.school.postservice.model.redis;

import faang.school.postservice.dto.redis.RedisCommentDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.SortedSet;
import java.util.TreeSet;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@RedisHash(value = "Post")
public class RedisPost implements Serializable {
    @Id
    private Long id;
    @TimeToLive
    private int ttl;
    @Version
    private long version;

    private String content;
    private Long authorId;
    private SortedSet<RedisCommentDto> comments = new TreeSet<>();
    private int likes;
    private LocalDateTime publishedAt;
    private LocalDateTime updatedAt;

    public void addComment(RedisCommentDto redisCommentDto) {
        comments.add(redisCommentDto);
    }

    public void removeComment(long commentId) {
        comments.stream().filter(redisCommentDto -> redisCommentDto.getId() == commentId)
                .findFirst()
                .ifPresent(comments::remove);
    }

    public void removeLastRedisCommentDto() {
        RedisCommentDto last = comments.last();
        comments.remove(last);
    }
}