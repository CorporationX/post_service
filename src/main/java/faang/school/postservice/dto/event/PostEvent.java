package faang.school.postservice.dto.event;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class PostEvent {
    private long authorId;
    private long postId;
    private List<Long> subscriberIds;
    private LocalDateTime createdAt;
}
