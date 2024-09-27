package faang.school.postservice.kafka.events;

import lombok.Builder;

@Builder
public record PostEvent(
        Long postId,
        Long authorId
) {}