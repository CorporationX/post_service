package faang.school.postservice.service;

import faang.school.postservice.model.ad.Ad;
import faang.school.postservice.repository.ad.AdRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdService {
    private final AdRepository adRepository;

    @Value("${ad-remover.list-size}")
    private final int listSize;

    @Value("${ad-remover.thread-count}")
    private final int threadCount;

    public void deleteOverdueAds() {
        List<Ad> ads = adRepository.findAll();
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        for (int i = 0; i < ads.size(); i+= listSize) {
            int index = i;
            executorService.execute(() -> filterAndClearAds(ads.subList(index, Math.min(index + listSize, ads.size()))));
        }
    }

    private void filterAndClearAds(List<Ad> ads) {
        LocalDateTime now = LocalDateTime.now();
        ads.stream()
                .filter(ad -> ad.getEndDate().isBefore(now))
                .forEach(ad -> adRepository.deleteById(ad.getId()));
    }

}
