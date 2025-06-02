package faang.school.postservice.dto.comment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentCacheDto {
    private Long id;
    private String content;
    private Long authorId;
    private Long postId;
    private int likeCount;
    private LocalDateTime createdAt;
}
