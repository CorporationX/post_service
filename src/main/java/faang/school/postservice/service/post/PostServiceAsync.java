package faang.school.postservice.service.post;

import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class PostServiceAsync {
    private final PostRepository postRepository;

    @Async("fixedThreadPool")
    @Transactional
    public void publishScheduledPostsAsyncInBatch(List<Post> posts) {
        var postIds = posts.stream()
                .map(Post::getId)
                .toList();

        postRepository.updatePostsAsPublished(postIds, LocalDateTime.now());
    }
}
