package faang.school.postservice.event.post;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostEvent {
    private long id;
    private long authorId;
    private long postId;
    private long userId;
    private LocalDateTime timestamp;
}