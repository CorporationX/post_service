package faang.school.postservice.dto.post;

import lombok.Builder;

import java.util.List;

@Builder
public record PostProcessEvent(
        Long postId,

        List<Long> followersIds
) {
}
