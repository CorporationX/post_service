package faang.school.postservice.model.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostViewEvent {
    private Long postId;
    private Long authorId;
    private Long actorId;
    private LocalDateTime receivedAt;
}
