package faang.school.postservice.service.impl.post.async;

import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.PostServiceAsync;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class PostServiceAsyncImpl implements PostServiceAsync {
    private final PostRepository postRepository;

    @Async("fixedThreadPool")
    public void publishScheduledPostsAsyncInBatch(List<Post> posts) {
       posts.forEach(post -> {
           post.setPublished(true);
           post.setPublishedAt(LocalDateTime.now());
       });
       postRepository.saveAll(posts);
    }
}
