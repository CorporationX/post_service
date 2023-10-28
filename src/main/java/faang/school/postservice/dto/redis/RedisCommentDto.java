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
public class RedisCommentDto {

    private long id;
    private String content;
    private long authorId;
    private int likes;
    private long postId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public void likeIncrement() {
        likes++;
    }

    public void likeDecrement() {
        likes--;
    }
}
