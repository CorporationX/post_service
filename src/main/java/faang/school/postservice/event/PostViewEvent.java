package faang.school.postservice.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostViewEvent {
    private UUID eventId;
    private long postId;
    private long authorId;
    private long userId;
    private LocalDateTime timestamp;
}