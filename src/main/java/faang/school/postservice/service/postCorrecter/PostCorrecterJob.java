package faang.school.postservice.service.postCorrecter;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PostCorrecterJob {
    private final PostCorrecterService postCorrecterService;

    @Scheduled(cron = "${ai-spelling.cron}")
    public void correctPosts() {
        log.info("Correcting posts started.");
        System.out.println("Begin...");
        postCorrecterService.correctUnpublishedPosts();
        System.out.println("end...");
        log.info("Correcting posts is over.");
    }
}
