package faang.school.postservice.service.post;

import faang.school.postservice.model.ad.Ad;
import faang.school.postservice.repository.ad.AdRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdService {

    private final AdRepository adRepository;

    @Value("${sizeof-sublist}")
    private int subListSize;

    public void deleteExpiredAds() {
        List<Ad> ads = (List<Ad>) adRepository.findAll();
        List<Ad> filteredAds = ads.stream()
                .filter(ad -> ad.getEndDate().isBefore(LocalDateTime.now())
                        || ad.getAppearancesLeft() == 0)
                .toList();

        if (filteredAds.isEmpty()) {
            log.info("Реклам с истёкшим сроком не обнаружено. Удаление не выполнено.");
        } else {
            ExecutorService executorService
                    = Executors.newFixedThreadPool(ads.size() / subListSize);

            executorService.submit(
                    () -> filteredAds.forEach(ad -> adRepository.deleteById(ad.getId())));
        }
    }
}
