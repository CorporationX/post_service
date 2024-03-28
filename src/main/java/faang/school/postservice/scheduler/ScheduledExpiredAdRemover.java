package faang.school.postservice.scheduler;

import faang.school.postservice.model.ad.Ad;
import faang.school.postservice.repository.ad.AdRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ScheduledExpiredAdRemover {

    @Value("${post.ad-remover.batch.size}")
    private int batchSize;

    private final ThreadPoolTaskExecutor scheduledRemoverThreadPoolExecutor;
    private final AdRepository adRepository;

    @Scheduled(cron = "${post.ad-remover.scheduler.every_day_cron}")
    public void removeExpiredAds() {
        LocalDateTime expiredTime = LocalDateTime.now();
        List<Ad> adList = adRepository.findExpiredAds();

        log.info("Started removing expired ads. Number of objects to be deleted: {}, Current time: {}", adList.size(), expiredTime);

        for (int i = 0; i < adList.size(); i += batchSize) {
            int endIndex = Math.min(i + batchSize, adList.size());
            List<Ad> subList = adList.subList(i, endIndex);
            scheduledRemoverThreadPoolExecutor.execute(() -> {
                List<Long> ids = subList.stream()
                        .map(Ad::getId)
                        .toList();
                adRepository.deleteAllById(ids);
            });
        }
        log.info("Expired ads removing are completed, Current time: {}", LocalDateTime.now());
    }
}