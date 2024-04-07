package faang.school.postservice.service.async;

import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AsyncPostService {

    private final PostRepository postRepository;

    @Async(value = "postPublishingThreadPool")
    public void publishThousandPosts(List<Post> posts, int start, int end) {
        List<Post> currentPosts = new ArrayList<>();
        for (int i = start; i < end; i++) {
            Post currentPost = posts.get(i);
            currentPost.setPublished(true);
            currentPost.setPublishedAt(LocalDateTime.now());
            currentPosts.add(currentPost);
        }
        postRepository.saveAll(currentPosts);
    }
}
