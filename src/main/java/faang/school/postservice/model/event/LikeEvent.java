package faang.school.postservice.model.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LikeEvent {
    private Long postId;
    private Long userId;
    private Long authorId;
    private LocalDateTime createdAt;
}
