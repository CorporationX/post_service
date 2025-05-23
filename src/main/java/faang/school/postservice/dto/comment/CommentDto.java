package faang.school.postservice.dto.comment;

import faang.school.postservice.model.CommentDtoStatus;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class CommentDto {
    private Long id;
    private CommentDtoStatus status;
    private Long postId;
    private String content;
    private Long authorId;
    private LocalDateTime createdAt;
    private List<Integer> likeIds;
}
