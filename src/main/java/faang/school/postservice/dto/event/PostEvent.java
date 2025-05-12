package faang.school.postservice.dto.event;


import lombok.Builder;

import java.util.List;

@Builder
public record PostEvent(
        Long authorId,
        List<Long> followeeIds,
        Long postId
        ) {
}
