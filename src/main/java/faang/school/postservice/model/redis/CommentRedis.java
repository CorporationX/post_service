package faang.school.postservice.model.redis;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommentRedis implements Comparable<CommentRedis> {

    private Long id;
    private Long authorId;
    private Long postId;
    private String content;
    private LocalDateTime updatedAt;

    @Override
    public int compareTo(CommentRedis object) {
        return object.getUpdatedAt().compareTo(updatedAt);
    }
}