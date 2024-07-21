package faang.school.postservice.initializer;

import faang.school.postservice.service.cashe.CacheService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CacheInitializer {
    private static final Logger logger = LoggerFactory.getLogger(CacheInitializer.class);
    private final CacheService cacheService;

    @PostConstruct
    public void init() {
        logger.info("Initializing cache...");
        try {
            cacheService.initializeCache();
            logger.info("Cache initialized successfully.");
        } catch (Exception e) {
            logger.error("Error during cache initialization", e);
            throw e;
        }
    }

    @Scheduled(fixedRateString = "${cache.refresh.rate:3600000}")
    public void refreshCache() {
        logger.info("Refreshing cache...");
        try {
            cacheService.refreshCache();
            logger.info("Cache refreshed successfully.");
        } catch (Exception e) {
            logger.error("Error during cache refresh", e);
        }
    }
}