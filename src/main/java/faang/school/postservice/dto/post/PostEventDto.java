package faang.school.postservice.dto.post;

import java.util.List;

public record PostEventDto(
        Long postId,
        Long authorId,
        List<Long> followeesIds
) {
}
