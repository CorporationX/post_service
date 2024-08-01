package faang.school.postservice.dto.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Builder
public class CommentEventDto extends EventDto implements Serializable {

    private Long commentAuthorId;

    private Long postAuthorId;

    private Long postId;

    private Long commentId;

    private String commentText;

    private LocalDateTime createdAt;
}
