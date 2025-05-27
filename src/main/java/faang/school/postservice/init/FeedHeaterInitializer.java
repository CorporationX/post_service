package faang.school.postservice.init;

import faang.school.postservice.service.feed.FeedHeater;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class FeedHeaterInitializer implements CommandLineRunner {

    private final FeedHeater feedHeater;

    @Override
    public void run(String... args) {
        log.info("Initialization: starting feed warming on application startup");
        try {
            feedHeater.heatFeeds();
        } catch (Exception e) {
            log.error("Error occurred during automatic feed warming on startup", e);
        }
    }
}
