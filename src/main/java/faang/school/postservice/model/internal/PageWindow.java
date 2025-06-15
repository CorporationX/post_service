package faang.school.postservice.model.internal;

import java.util.List;

public record PageWindow(
        List<Long> userIds,
        long totalLikes
) {}
