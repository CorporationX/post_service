package faang.school.postservice.initializer;

import faang.school.postservice.service.cashe.CacheService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class CacheInitializer {
    private final CacheService cacheService;
    private boolean cacheInitialized = false;

    @PostConstruct
    public void init() {
        log.info("Initializing cache");
        try {
            cacheService.initializeCache();
            cacheInitialized = true;
            log.info("Cache initialized successfully.");
        } catch (Exception e) {
            log.error("Error during cache initialization", e);
            throw e;
        }
    }

    @Scheduled(fixedRateString = "${spring.data.cache.refresh.rate}")
    public void refreshCache() {
        if (cacheInitialized) {
            cacheInitialized = false;
            return;
        }
        log.info("Refreshing cache");
        try {
            cacheService.initializeCache();
            log.info("Cache refreshed successfully.");
        } catch (Exception e) {
            log.error("Error during cache refresh", e);
        }
    }

    @PreDestroy
    public void clearCache() {
        log.info("Clearing cache before shutdown");
        try {
            cacheService.clearCache();
            log.info("Cache cleared successfully.");
        } catch (Exception e) {
            log.error("Error during cache clearing", e);
        }
    }
}