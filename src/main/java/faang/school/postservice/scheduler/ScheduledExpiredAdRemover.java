package faang.school.postservice.scheduler;

import faang.school.postservice.model.ad.Ad;
import faang.school.postservice.repository.ad.AdRepository;
import faang.school.postservice.service.ad.AdService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;


import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class ScheduledExpiredAdRemover {

    @Value("${post.ad-remover.max-list-size}")
    private int maxListSize;
    private final AdService adService;
    private final AdRepository adRepository;

    @Scheduled(cron = "${post.ad-remover.scheduler.cron}")
    public void deleteExpiredAdsScheduled() {
        try {
            log.info("Начинаем удаление устаревших объявлений...");
            List<Ad> adsToDelete = adRepository.findAllExpiredAds();
            if (adsToDelete.isEmpty()) {
                log.info("Не найдено устаревших объявлений для удаления.");
                return;
            }

            log.info("Найдено {} устаревших объявлений для удаления.", adsToDelete.size());
            List<List<Ad>> partitionedAdsToDelete = ListUtils.partition(adsToDelete, maxListSize);
            log.info("Объявления разбиты на {} группы для удаления.", partitionedAdsToDelete.size());
            partitionedAdsToDelete.forEach(adService::deleteAds);
            log.info("Удаление устаревших объявлений завершено успешно.");

        } catch (Exception e) {
            log.error("Произошла ошибка при удалении устаревших объявлений: {}", e.getMessage(), e);
            throw new RuntimeException("Ошибка при удалении устаревших объявлений", e);
        }
    }
}