package faang.school.postservice.scheduler;

import faang.school.postservice.api.PostCorrector;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CorrectorScheduler {
    private final PostRepository postRepository;
    private final PostCorrector postCorrector;

    @PostConstruct
    void initialCheck() {
        correctNotPublishedPostsContent();
    }

    @Scheduled(cron = "${post.grammar-checker.scheduler.cron}")
    public void correctNotPublishedPostsContent() {
        List<Post> posts = postRepository.findAllNotPublishedPosts();
        posts.forEach(post -> {
            String correctedContent = postCorrector.correctPost(post.getContent());
            post.setContent(correctedContent);
        });
        postRepository.saveAll(posts);
    }
}
