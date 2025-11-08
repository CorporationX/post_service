package faang.school.postservice.service.ad;

import faang.school.postservice.repository.ad.AdRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@RequiredArgsConstructor
@Service
public class AdServiceImpl implements AdService {
    private final AdRepository adRepository;

    @Transactional
    @Override
    public void removeAds() {
        log.debug("Start processing remove ad");
        LocalDateTime now = LocalDateTime.now();
        adRepository.deleteAllByEndDate(now);
        log.debug("Successful delete Ads");
    }
}