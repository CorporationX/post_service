package faang.school.postservice.scheduler;

import faang.school.postservice.service.dictionary.OffensiveDictionaryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OffensiveDictionaryScheduler {

    private final OffensiveDictionaryService offensiveDictionaryService;

    @Scheduled(cron = "${dictionary.offensive.remote.scheduler.cron}")
    public void updateOffensiveDictionary() {
        log.info("updateOffensiveDictionary() - start.");
        offensiveDictionaryService.updateOffensiveDictionary();
        log.info("updateOffensiveDictionary() - finish.");
    }
}
