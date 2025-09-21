package faang.school.postservice.dto.feed;

import java.util.List;

public record CacheWarmupTask(List<Long> subscriberIds) {}
