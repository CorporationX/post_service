package faang.school.postservice.service;

import faang.school.postservice.model.ad.Ad;
import faang.school.postservice.repository.ad.AdRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j
public class AdService {
    private AdRepository adRepository;

    public List<Ad> FindExpiredAds() {
        log.info("Scheduled expired ads removing");
        List<Ad> adList = new ArrayList<>();
        Iterable<Ad> ads = adRepository.findAll();
        ads.forEach(adList::add);

        return adList.stream().filter(ad -> ad.getEndDate().isBefore(LocalDateTime.now())).toList();
    }

    @Async("adRemoverThreadPool")
    @Transactional
    public void removeExpiredAds(List<Ad> expiredAds) {
        adRepository.deleteAll(expiredAds);
    }
}
