package faang.school.postservice.service.ad;

import faang.school.postservice.model.ad.Ad;
import faang.school.postservice.repository.ad.AdRepository;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdService {
    private final AdRepository adRepository;

    @Value("${post.ad-remover.batch-size}")
    @Setter
    private int batchSize;

    public List<Ad> findAll() {
        return adRepository.findAll();
    }

    public void deleteByIds(List<Long> ids) {
        adRepository.deleteByIds(ids);
    }

    @Async
    public void deleteExpiredAds() {
        List<Long> idsOfExpiredAds = filteringExpiredAds(findAll());
        if (!idsOfExpiredAds.isEmpty()) {
            ListUtils.partition(idsOfExpiredAds, batchSize)
                    .parallelStream()
                    .forEach(this::deleteByIds);
        }
    }

    private List<Long> filteringExpiredAds(List<Ad> ads) {
        LocalDateTime currentDateTime = LocalDateTime.now();
        return ads.stream()
                .filter(ad -> ad.getAppearancesLeft() == 0 || ad.getEndDate().isBefore(currentDateTime))
                .map(Ad::getId)
                .toList();
    }
}