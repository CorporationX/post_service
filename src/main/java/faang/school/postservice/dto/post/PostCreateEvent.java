package faang.school.postservice.dto.post;

import java.util.List;

public record PostCreateEvent(
        long id,
        List<Long> followerIds
) {
}
