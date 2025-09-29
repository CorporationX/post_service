package faang.school.postservice.dto.user.follower;

import java.util.List;

public record FollowersPage(
        List<Long> ids,
        String nextCursor
) {
}
