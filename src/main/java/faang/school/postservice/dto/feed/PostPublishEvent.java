package faang.school.postservice.dto.feed;

import lombok.Builder;

import java.util.List;

@Builder
public record PostPublishEvent(
        Long postId,
        List<Long> followerIds
) {}
