package faang.school.postservice.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostViewEvent extends Event {
    private Long postId;
    private Long userId;
    private Long authorId;
    private LocalDateTime viewedAt;
}
