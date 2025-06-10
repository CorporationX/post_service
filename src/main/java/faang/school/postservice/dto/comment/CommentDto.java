package faang.school.postservice.dto.comment;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
public class CommentDto {
    private Long id;
    private String content;
    private Long authorId;
    private Long postId;
}