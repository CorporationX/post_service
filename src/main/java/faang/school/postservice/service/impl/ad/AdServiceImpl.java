package faang.school.postservice.service.impl.ad;

import faang.school.postservice.model.ad.Ad;
import faang.school.postservice.repository.ad.AdRepository;
import faang.school.postservice.service.AdService;
import faang.school.postservice.service.AdServiceAsync;
import faang.school.postservice.service.AdService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.ListUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdServiceImpl implements AdService {
    private final AdRepository adRepository;
    private final AdServiceAsync adServiceAsync;

    @Override
    public void removeExpiredAds(int batchSize) {
        var ads = adRepository.findAllByEndDateBefore(LocalDateTime.now());
        if (!ads.isEmpty()) {
            ListUtils.partition(ads, batchSize).forEach(adServiceAsync::deleteExpiredAdsByBatch);
        }
    }

    @Async("adRemoverThreadPool")
    public void deleteExpiredAdsByBatch(List<Ad> ads) {
        adRepository.deleteAllInBatch(ads);
    }
}
