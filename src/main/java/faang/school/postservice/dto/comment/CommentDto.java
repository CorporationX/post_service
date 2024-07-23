package faang.school.postservice.dto.comment;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class CommentDto {
    private Long id;
    private String content;
    private Long authorId;
    private List<Long> likesId;
    private Long postId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
