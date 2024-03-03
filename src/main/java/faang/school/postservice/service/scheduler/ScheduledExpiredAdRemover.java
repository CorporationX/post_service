package faang.school.postservice.service.scheduler;

import faang.school.postservice.model.ad.Ad;
import faang.school.postservice.service.AdService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ScheduledExpiredAdRemover {
    private final AdService adService;


    @Scheduled(cron = "${post.ad-remover.scheduler.cron}", zone = "${post.ad-remover.scheduler.zone}")
    public void removeExpiredAds() {
        Optional<List<List<Ad>>> expiredAds = adService.findExpiredAds();
        expiredAds.ifPresent(lists -> lists.forEach(adService::removeExpiredAds));
    }
}
