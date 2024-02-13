package faang.school.postservice.service;

import faang.school.postservice.model.ad.Ad;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ScheduledExpiredAdRemover {
    private final AdService adService;
    @Value("${post-service.scheduled-expired-ad-remover.batch-size}")
    private int expiredAdBatchSize;

    @Scheduled(cron = "${post-service.scheduled-expired-ad-remover.cron}", zone = "${post-service.scheduled-expired-ad-remover.zone}")
    public void removeExpiredAds() {
        List<Ad> expiredAds = adService.FindExpiredAds();
        if (!expiredAds.isEmpty()) {
            List<List<Ad>> expiredAdBatches = ListUtils.partition(expiredAds, expiredAdBatchSize);
            expiredAdBatches.forEach(adService::removeExpiredAds);
        }
    }
}
