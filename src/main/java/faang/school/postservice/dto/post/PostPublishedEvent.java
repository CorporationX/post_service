package faang.school.postservice.dto.post;

import java.util.List;

public record PostPublishedEvent(
        Long postId,
        Long authorId,
        List<Long> followersIds
) {
}
