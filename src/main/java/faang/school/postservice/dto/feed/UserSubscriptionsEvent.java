package faang.school.postservice.dto.feed;

import java.util.List;
import java.util.Map;

public record UserSubscriptionsEvent(
        Map<Long, List<Long>> userSubscriptions
) {}
