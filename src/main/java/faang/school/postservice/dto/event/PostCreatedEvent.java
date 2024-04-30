package faang.school.postservice.dto.event;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Alexander Bulgakov
 */

public record PostCreatedEvent(
        long postId,
        List<Long> subscriberIds,
        LocalDateTime createdAt) {
}
