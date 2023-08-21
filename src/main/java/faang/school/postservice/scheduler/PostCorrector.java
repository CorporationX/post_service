package faang.school.postservice.scheduler;

import faang.school.postservice.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PostCorrector {
    private final PostService postService;
    @Async
    @Scheduled(cron = "${post.corrector.cron}")
    public void correctPosts(){
		postService.correctPosts();
    }
}
