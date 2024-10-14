package faang.school.postservice.dto.comment;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class CommentDto implements Serializable, Comparable<CommentDto> {
    private String content;
    private long authorId;
    private long likesCount;
    private long postId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Override
    public int compareTo(CommentDto o) {
        return o.getCreatedAt().compareTo(this.getCreatedAt());
    }
}
