package faang.school.postservice.service.post.async;

import faang.school.postservice.model.Post;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class PostServiceAsyncImpl implements PostServiceAsync {

    @Async("fixedThreadPool")
    public void publishScheduledPostsAsyncInBatch(List<Post> posts) {
       posts.forEach(post -> {
           post.setPublished(true);
           post.setPublishedAt(LocalDateTime.now());
       });
    }
}
