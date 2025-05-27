package faang.school.postservice.dto.comment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentDtoResponse {

    private Long commentId;
    private Long postId;
    private Long authorId;
    private String content;
    private String createData;
    private String updateData;
}
