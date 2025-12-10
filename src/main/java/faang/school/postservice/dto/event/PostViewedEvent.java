package faang.school.postservice.dto.event;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PostViewedEvent {
    private long postId;
}
