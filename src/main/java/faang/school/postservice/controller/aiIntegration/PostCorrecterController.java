package faang.school.postservice.controller.aiIntegration;

import com.fasterxml.jackson.core.JsonProcessingException;
import faang.school.postservice.service.postCorrecter.PostCorrecterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class PostCorrecterController {

    private final PostCorrecterService postCorrecterService;

    @Scheduled(cron = "${post-correcter.cron}")
    public void correctPostsScheduled() throws JsonProcessingException {
        log.info("Post correction job is started");
        postCorrecterService.correctUnpublishedPosts();
        log.info("Post correction job is ended");
    }
}
