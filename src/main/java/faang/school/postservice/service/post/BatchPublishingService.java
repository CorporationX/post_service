package faang.school.postservice.service.post;

import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BatchPublishingService {

    private final PostRepository postRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void publishBatch(List<Post> batch) {
        log.debug("Publishing a batch of {} posts in the stream {}",
                batch.size(), Thread.currentThread().getName());

        try {
            batch.forEach(post -> {
                post.setPublished(true);
                post.setPublishedAt(LocalDateTime.now());
            });

            postRepository.saveAll(batch);
            log.debug("The batch of {} posts was successfully published", batch.size());

        } catch (Exception e) {
            log.error("Error publishing batch of {} posts: {}", batch.size(), e.getMessage());
        }
    }
}