package faang.school.postservice.dto.event;

import lombok.Builder;

@Builder
public record ViewEvent(
        long viewId,
        long postId
) {}
