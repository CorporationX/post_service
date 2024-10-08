package faang.school.postservice.model.event;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class LikeEvent {
    private Long postId;
    private Long userId;
    private Long authorId;
    private LocalDateTime dateLike;
}
