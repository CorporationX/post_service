package faang.school.postservice.service.post;

import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class BatchPublisher {
    private final PostRepository postRepository;

    @Transactional
    public void publishBatch(List<Post> batch) {
        LocalDateTime now = LocalDateTime.now();

        for (Post post : batch) {
            post.setPublished(true);
            post.setPublishedAt(now);
        }

        postRepository.saveAll(batch);
    }
}
