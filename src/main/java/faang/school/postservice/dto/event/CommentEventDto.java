package faang.school.postservice.dto.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentEventDto {
    private Long id;
    private Long authorId;
    private Long postId;
    private Long receiverId;
    private LocalDateTime createdAt;
}
