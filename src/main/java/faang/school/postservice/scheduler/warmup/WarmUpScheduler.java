package faang.school.postservice.scheduler.warmup;

import faang.school.postservice.service.cash.CacheWarmerService;
import org.springframework.scheduling.annotation.Scheduled;

public record WarmUpScheduler(CacheWarmerService cacheWarmerService) {
    @Scheduled(cron = "${spring.data.cache.warmup.schedule}")
    public void scheduledWarmUp() {
        cacheWarmerService.warmUpCache();
    }
}