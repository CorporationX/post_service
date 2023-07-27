package faang.school.postservice.dto.comment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Builder
public class CommentDto {
    private long id;
    private long authorId;
    private long postId;//?
    private String content;
    private LocalDateTime createdAt;
}
