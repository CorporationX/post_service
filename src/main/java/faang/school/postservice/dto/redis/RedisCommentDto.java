package faang.school.postservice.dto.redis;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RedisCommentDto implements Comparable<RedisCommentDto>{
    private long id;
    private long authorId;
    private long postId;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Override
    public int compareTo(RedisCommentDto redisCommentDto) {
        return createdAt.compareTo(redisCommentDto.createdAt);
    }
}
