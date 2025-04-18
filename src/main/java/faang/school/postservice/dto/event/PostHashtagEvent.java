package faang.school.postservice.dto.event;

import lombok.Builder;

@Builder
public record PostHashtagEvent(
        String hashtagName,
        Long postId
) {
}
