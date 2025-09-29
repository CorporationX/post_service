package faang.school.postservice.service.feed.warmup;

import faang.school.postservice.dto.user.feed.HeatUserTask;

public interface FeedWarmupService {
    void warmUpUser(HeatUserTask task);
}
