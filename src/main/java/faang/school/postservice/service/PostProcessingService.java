package faang.school.postservice.service;

import faang.school.postservice.exception.DataValidationException;
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
public class PostProcessingService {

    private final PostRepository postRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void publishBatch(List<Post> batch) {
        if (batch == null || batch.isEmpty()) {
            log.error("в списке не содержится постов");
            throw new DataValidationException("список постов пуст");
        }

        for (Post post : batch) {
            post.setPublished(true);
            post.setPublishedAt(LocalDateTime.now());
        }

        postRepository.saveAll(batch);
        log.info("Опубликовано {} постов с {} по {} id.",
                batch.size(), batch.get(0).getId(), batch.get(batch.size() - 1).getId());
    }
}
