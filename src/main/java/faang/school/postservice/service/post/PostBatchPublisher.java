package faang.school.postservice.service.post;

import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class PostBatchPublisher {

    private final PostRepository postRepository;

    public void publishPosts(List<Post> posts) {
        LocalDateTime publishTime = LocalDateTime.now();

        posts.forEach(post -> {
            post.setPublished(true);
            post.setPublishedAt(publishTime);
        });

        postRepository.saveAll(posts);
        log.info("Successfully published {} posts, publish time: {}", posts.size(), publishTime);
    }
}
