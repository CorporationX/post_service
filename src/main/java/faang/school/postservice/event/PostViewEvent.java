package faang.school.postservice.event;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class PostViewEvent {

    private long postId;
    private long ownerId;
    private long viewerId;
    private LocalDateTime postViewTime;
}
