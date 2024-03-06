package faang.school.postservice.service;

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
public class PostService {

    private final PostRepository postRepository;

    public void publishScheduledPosts() {
        List<Post> posts = postRepository.findReadyToPublish();

        for (int i = 0; i < posts.size(); i += 1000) {
            int finalI = i;
            if (i + 1000 < posts.size()) {
                publishThousandPosts(posts, finalI, finalI + 1000);
            } else {
                publishThousandPosts(posts, finalI, posts.size());
            }
        }
    }

    @Async(value = "poolForScheduled")
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
