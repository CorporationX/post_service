package faang.school.postservice.service.async;

import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostAsyncService {
    private final PostRepository postRepository;

    @Async("taskExecutor")
    public void publishPosts(List<Post> posts) {
        posts.forEach(post -> {
            post.setPublished(true);
            post.setPublishedAt(LocalDateTime.now());
            postRepository.save(post);
            log.info(Thread.currentThread().getName() + "Published post: " + post);
        });
        log.info("Published " + posts.size() + " posts");
    }
}