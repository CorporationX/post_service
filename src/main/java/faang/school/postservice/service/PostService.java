package faang.school.postservice.service;

import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.async.AsyncPostService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final AsyncPostService asyncPostService;

    @Value("${post.publisher.scheduler.batch-size}")
    private int batchSize;

    public void publishScheduledPosts() {
        List<Post> posts = postRepository.findReadyToPublish();

        for (int i = 0; i < posts.size(); i += batchSize) {
            int finalI = i;
            if (i + batchSize < posts.size()) {
                asyncPostService.publishThousandPosts(posts, finalI, finalI + batchSize);
            } else {
                asyncPostService.publishThousandPosts(posts, finalI, posts.size());
            }
        }
    }
}
