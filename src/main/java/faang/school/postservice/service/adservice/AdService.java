package faang.school.postservice.service.adservice;

import faang.school.postservice.model.ad.Ad;
import faang.school.postservice.repository.ad.AdRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdService {
    public final AdRepository adRepository;

    @Value("${batchSize.batch}")
    private int batchSize;

    public void deleteAdsWhichEndPaidPeriod() {
        log.info("Запускаем процесс поиска завершившихся рекламных кампаний");
        List<Ad> allAds = adRepository.findAll();
        if (allAds == null) {
            throw new RuntimeException("Список рекламный кампаний пуст");
        }
        List<Long> removeAdsIds = allAds.stream().filter(ad -> LocalDateTime.now().isAfter(ad.getEndDate()) || ad.getAppearancesLeft() == 0)
                .map(Ad::getId).toList();
        if (removeAdsIds.isEmpty()) {
            log.info("Завершившихся рекламных кампаний не найдено");
            return;
        }
        log.info("Завершившиеся рекламные кампании найдены, начинает процесс их удаления из БД");
        List<List<Long>> partition = ListUtils.partition(removeAdsIds, batchSize);
        for (List<Long> partListAdIds : partition) {
            startDeleteAds(partListAdIds);
        }
        log.info("Рекламные кампании у которых закончил срок их действия успешно удалены");
    }

    @Async("executorService")
    public void startDeleteAds(List<Long> adIds) {
        adRepository.deleteAllById(adIds);
    }
}
