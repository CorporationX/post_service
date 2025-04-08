package faang.school.postservice.dto.comment;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CommentResponseDto {

    private long id;

    private String content;

    private long authorId;

    private long postId;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
