package faang.school.postservice.service;

import faang.school.postservice.config.ThreadPoolConfig;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final ThreadPoolConfig threadPoolConfig;
    private final ExecutorService poolForScheduled = threadPoolConfig.poolForScheduled();

    public void publishScheduledPosts() {
        List<Post> posts = postRepository.findReadyToPublish();

        for (int i = 0; i < posts.size(); i += 1000) {
            int finalI = i;
            if (i + 1000 < posts.size()) {
                poolForScheduled.execute(() -> publishThousandPosts(posts, finalI, finalI + 1000));
            } else
                poolForScheduled.execute(() -> publishThousandPosts(posts, finalI, posts.size()));
        }
    }

    private void publishThousandPosts(List<Post> posts, int start, int end) {
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
