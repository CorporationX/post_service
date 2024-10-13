package faang.school.postservice.scheduler;

import faang.school.postservice.api.PostCorrector;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class CorrectorScheduler {
    private final PostRepository postRepository;
    private final PostCorrector postCorrector;

//    @PostConstruct
//    void initialCheck() {
//        correctNotPublishedPostsContent();
//    }

    @Scheduled(cron = "${post.grammar-checker.scheduler.cron}")
    public void correctNotPublishedPostsContent() {
        try {
            List<Post> posts = postRepository.findAllNotPublishedPosts();
            posts.forEach(postCorrector::correctPost);
            postRepository.saveAll(posts);
        } catch (Exception e) {
            log.error("Exception was thrown when trying to correct post", e);
            throw new RuntimeException(e);
        }
    }
}
