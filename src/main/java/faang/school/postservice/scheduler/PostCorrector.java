package faang.school.postservice.scheduler;

import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PostCorrector {
    private final PostService postService;

    @Scheduled(cron = "")
    public void correctPosts() {
        postService.correctNonPublishedPosts();


    }
}
