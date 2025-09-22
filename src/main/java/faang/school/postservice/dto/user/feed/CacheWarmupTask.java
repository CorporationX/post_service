package faang.school.postservice.dto.user.feed;

import java.util.List;

public record CacheWarmupTask(
        List<Long> userIds
) {
}
