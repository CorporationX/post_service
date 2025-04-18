package faang.school.postservice.model.event.post;

import faang.school.postservice.model.event.Event;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class PostEvent implements Event {
    private Long postId;
    private Long authorId;
    private Long viewerId;
    private LocalDateTime timestamp;
}
