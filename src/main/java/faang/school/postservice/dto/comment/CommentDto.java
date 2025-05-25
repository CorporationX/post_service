package faang.school.postservice.dto.comment;

import faang.school.postservice.model.CommentDtoStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto {
    private Long id;
    private CommentDtoStatus status;
    private Long postId;
    private String content;
    private Long authorId;
    private LocalDateTime createdAt;
    private List<Integer> likeIds;
}
