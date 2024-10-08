package faang.school.postservice.kafka.events;

import lombok.Builder;

@Builder
public record PostViewEvent(
        Long postId
) {}