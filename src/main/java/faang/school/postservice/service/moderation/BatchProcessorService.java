package faang.school.postservice.service.moderation;

import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BatchProcessorService {

    private final PostRepository postRepository;
    private final ModerationDictionary moderationDictionary;

    @Transactional(propagation = Propagation.REQUIRED)
    public void processBatch(List<Post> batch) {
        batch.forEach(post -> {
            boolean hasBadWords = moderationDictionary.containsBadWord(post.getContent());
            post.setVerified(!hasBadWords);
            post.setVerifiedAt(LocalDateTime.now());
            logModerationResult(post, hasBadWords);
        });
        postRepository.saveAll(batch);
    }

    private void logModerationResult(Post post, boolean hasBadWords) {
        if (hasBadWords) {
            log.warn("Post moderation FAILED. Post ID: {}, Author ID: {}. Contains bad words. Marked as unverified.",
                    post.getId(), post.getAuthorId());
        } else {
            log.info("Post moderation PASSED. Post ID: {}, Author ID: {}. Marked as verified.",
                    post.getId(), post.getAuthorId());
        }
    }
}
