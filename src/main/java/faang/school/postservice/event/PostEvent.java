package faang.school.postservice.event;

import lombok.Builder;

import java.util.List;

@Builder
public record PostEvent(
        Long postId,
        Long authorId,
        String content,
        List<Long> followersIds
) {
}