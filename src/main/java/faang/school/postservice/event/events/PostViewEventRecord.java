package faang.school.postservice.event.events;

import lombok.Builder;

@Builder
public record PostViewEventRecord(
        Long postId
) {
}
