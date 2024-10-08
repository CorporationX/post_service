package faang.school.postservice.service.impl.ad;

import faang.school.postservice.model.ad.Ad;
import faang.school.postservice.repository.ad.AdRepository;
import faang.school.postservice.service.AdService;
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
        if (!ads.isEmpty()) {
            ListUtils.partition(ads, batchSize).forEach(this::deleteExpiredAdsByBatch);
        }
    }

    @Override
    @Async("adRemoverThreadPool")
    public void deleteExpiredAdsByBatch(List<Ad> ads) {
        adRepository.deleteAllInBatch(ads);
    }
}
