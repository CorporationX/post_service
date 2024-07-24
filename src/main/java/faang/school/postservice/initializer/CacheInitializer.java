package faang.school.postservice.initializer;

import faang.school.postservice.service.cashe.CacheService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class CacheInitializer {
    private final CacheService cacheService;

    @PostConstruct
    public void init() {
        log.info("Initializing cache");
        try {
            cacheService.initializeCache();
            log.info("Cache initialized successfully.");
        } catch (Exception e) {
            log.error("Error during cache initialization", e);
            throw e;
        }
    }

    @Scheduled(fixedRateString = "${cache.refresh.rate:3600000}")
    public void refreshCache() {
        log.info("Refreshing cache");
        try {
            cacheService.refreshCache();
            log.info("Cache refreshed successfully.");
        } catch (Exception e) {
            log.error("Error during cache refresh", e);
        }
    }
}