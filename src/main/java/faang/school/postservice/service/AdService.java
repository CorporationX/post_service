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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
@Slf4j
public class AdService {
    private AdRepository adRepository;

    @Value("${post-service.scheduled-expired-ad-remover.batch-size}")
    private int expiredAdBatchSize;

    public Optional<List<List<Ad>>> findExpiredAds() {
        log.info("Scheduled expired ads removing");
        List<Ad> adList = new ArrayList<>();
        Iterable<Ad> ads = adRepository.findAll();
        ads.forEach(adList::add);

        List<Ad> expiredAds = adList.stream().filter(ad -> ad.getEndDate().isBefore(LocalDateTime.now())).toList();
        if (!expiredAds.isEmpty()) {
            return Optional.of(ListUtils.partition(expiredAds, expiredAdBatchSize));
        }
        return Optional.empty();
    }

    @Async("adRemoverThreadPool")
    @Transactional
    public void removeExpiredAds(List<Ad> expiredAds) {
        adRepository.deleteAll(expiredAds);
    }
}
