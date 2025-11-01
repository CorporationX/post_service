package faang.school.postservice.service.ad;

import faang.school.postservice.model.ad.Ad;
import faang.school.postservice.repository.ad.AdRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class ProcessingAd {
    private final AdRepository adRepository;

    @Async(value = "taskExecutor")
    @Transactional
    public void processingAd(List<Ad> ads) {
        log.debug("Start processing remove ad");
        List<Long> removeAd = ads.stream()
                .filter(ad -> ad.getAppearancesLeft() == 0 || ad.getEndDate().isBefore(LocalDateTime.now()))
                .map(Ad::getId)
                .toList();
        if (!removeAd.isEmpty()) {
            adRepository.deleteByIdsNative(removeAd);
            log.debug("End remove ad. Count removed ad {}", removeAd.size());
        }
    }
}