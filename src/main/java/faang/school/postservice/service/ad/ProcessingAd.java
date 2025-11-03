package faang.school.postservice.service.ad;

import faang.school.postservice.repository.ad.AdRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class ProcessingAd {
    private final AdRepository adRepository;

    @Async(value = "taskExecutor")
    @Transactional
    public void processingAd(List<Long> ads) {
        log.debug("Start processing remove ad");
        if (!ads.isEmpty()) {
            adRepository.deleteAllByIdInBatch(ads);
            log.debug("End remove ad. Count removed ad {}", ads.size());
        }
    }
}