package faang.school.postservice.dto.feed;

import java.util.List;

public record UserFeedHeatDto(
        Long userId,
        List<Long> followerIds
) {
}
