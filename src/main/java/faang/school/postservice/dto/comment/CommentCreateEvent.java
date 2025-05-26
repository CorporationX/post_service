package faang.school.postservice.dto.comment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentCreateEvent {

    private Long postId;
    private Long authorId;
    private Long commentId;
    private LocalDateTime createdAt;
}
