package faang.school.postservice.service.ad;

import faang.school.postservice.model.ad.Ad;
import faang.school.postservice.repository.ad.AdRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.ListUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdServiceImpl implements AdService {
    private final AdRepository adRepository;

    @Override
    @Transactional
    public void removeExpiredAds(int batchSize) {
        var ads = adRepository.findAllByEndDateBefore(LocalDateTime.now());

        if (ads.isEmpty()) {
            return;
        }

        ListUtils.partition(ads, batchSize).forEach(this::deleteExpiredAdsByBatch);
    }

    @Async("adRemoverThreadPool")
    public void deleteExpiredAdsByBatch(List<Ad> ads) {
        adRepository.deleteAllInBatch(ads);
    }
}
