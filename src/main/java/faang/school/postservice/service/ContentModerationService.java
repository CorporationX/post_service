package faang.school.postservice.service;

import faang.school.postservice.exception.PostModerationException;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.scheduler.post.moderation.AhoCorasickContentChecker;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class ContentModerationService {
    private final PostRepository postRepository;
    private final AhoCorasickContentChecker contentChecker;

    @Transactional
    public CompletableFuture<Void> checkContentAndModerate(Post post) throws PostModerationException {
        boolean hasBadContent = contentChecker.containsBadContent(post.getContent());
        if (hasBadContent) {
            post.setVerified(false);
            post.setDeleted(true);
        } else {
            post.setVerified(true);
            post.setVerifiedAt(LocalDateTime.now());
        }
        postRepository.save(post);
        return CompletableFuture.completedFuture(null);
    }
}
