package faang.school.postservice.service.postCorrecter;

import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class PostCorrecterJob {
    private PostCorrecterService postCorrecterService;

    @Scheduled(cron = "${ai-spelling.cron}")
    public void correctPosts() {
        log.info("Correcting posts started.");
        postCorrecterService.correctUnpublishedPosts();
        log.info("Correcting posts is over.");
    }
}
