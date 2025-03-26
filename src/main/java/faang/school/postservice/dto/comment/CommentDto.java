package faang.school.postservice.dto.comment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentDto {
    private Long id;
    private String content;
    private Long authorId;
    private int likeCount;
    private Long postId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
