package faang.school.postservice.dto.post;

import java.util.List;

public record PostPublishedEvent(
        List<Long> subscriberIds
) {
}
