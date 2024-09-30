package faang.school.postservice.service.ad;

import faang.school.postservice.model.ad.Ad;
import faang.school.postservice.repository.ad.AdRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdServiceImpl implements AdService {
    private final AdRepository adRepository;

    @Override
    @Transactional
    public void removeExpiredAds(int batchSize) {
        var ads = adRepository.findAll().stream()
                .filter(ad ->
                        LocalDateTime.now().isAfter(ad.getEndDate()) ||
                        ad.getAppearancesLeft() == 0)
                .toList();

        if (ads.isEmpty()) {
            log.info("No expired ads");
            return;
        }

        List<List<Ad>> partitionedAd = ListUtils.partition(ads, batchSize);
        partitionedAd.forEach(this::deleteExpiredAdsByBatch);
    }

    @Async("adRemoverExecutorService")
    public void deleteExpiredAdsByBatch(List<Ad> ads) {
        adRepository.deleteAllInBatch(ads);
        ads.forEach(ad -> log.info("Deleting expired ad with ID: {}", ad.getId()));
    }
}
