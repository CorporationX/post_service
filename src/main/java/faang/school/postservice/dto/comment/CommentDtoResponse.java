package faang.school.postservice.dto.comment;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentDtoResponse {
    private Long id;
    private String content;
    private Long authorId;
    private LocalDateTime createdAt;
}
