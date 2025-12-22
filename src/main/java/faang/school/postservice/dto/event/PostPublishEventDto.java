package faang.school.postservice.dto.event;

import lombok.Builder;

import java.util.List;

@Builder
public record PostPublishEventDto(
        Long postId,
        Long authorId,
        String content,
        List<Long> subscriberIds
) {
}
