package faang.school.postservice.dto.kafka.event;

import lombok.Builder;

@Builder
public record PostViewEventDto(
    long postId,
    long userId
) {
}
