package faang.school.postservice.event;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LikeEvent {
    private Long postId;
    private Long authorId;
    private Long likedByUserId;
    private LocalDateTime timestamp;
}
