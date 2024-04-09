package faang.school.postservice.service;


import faang.school.postservice.model.ad.Ad;
import faang.school.postservice.repository.ad.AdRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdService {
    private final AdRepository adRepository;

    @Value("${batchSize.batch}")
    private int adsBatchSize;

    @Async("executor")
    public void removeExpiredAdsAsync(List<Long> adsToRemove) {
        adRepository.deleteAllById(adsToRemove);
    }

    @Transactional
    public void removeExpiredAds() {
        log.info("Начинаем процесс удаления просроченной рекламы.");
        List<Long> adsToRemove = StreamSupport.stream(adRepository.findAll().spliterator(), false)
                .filter(ad -> ad.getEndDate().isBefore(LocalDateTime.now()) || ad.getAppearancesLeft() == 0)
                .map(Ad::getId)
                .toList();

        if (adsToRemove.isEmpty()) {
            log.info("Нет объявлений с истекшим сроком действия, которые можно было бы удалить");
            return;
        }

        List<List<Long>> adsPartitions = ListUtils.partition(adsToRemove, adsBatchSize);
        for (List<Long> partition : adsPartitions) {
            removeExpiredAdsAsync(partition);
            log.info("Удаление просроченной рекламы задано для всех подсписков.");
        }
    }
}