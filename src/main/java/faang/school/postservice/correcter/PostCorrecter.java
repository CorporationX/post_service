package faang.school.postservice.correcter;

import faang.school.postservice.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PostCorrecter {

    private final PostService postService;

    @Scheduled(cron = "${post.correcter.scheduler.cron}")
    public void correctPosts() {
        postService.correctUnpublishedPosts();
    }
}
