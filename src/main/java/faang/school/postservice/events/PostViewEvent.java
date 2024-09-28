package faang.school.postservice.events;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
public class PostViewEvent extends Event {
    private Long postId;
    private Long userId;
    private Long authorId;
    private LocalDateTime viewedAt;
}
