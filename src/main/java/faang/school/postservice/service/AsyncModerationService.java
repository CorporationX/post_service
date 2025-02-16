package faang.school.postservice.service;

import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.validation.ModerationDictionary;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@RequiredArgsConstructor
@Service
public class AsyncModerationService {
    private final PostRepository postRepository;
    private final ModerationDictionary moderationDictionary;
    private final AiModerationService aiModerationService;

    @Async("commonTaskExecutor")
    public CompletableFuture<Void> moderateThreadAsync(List<Post> posts) {
        try {
            moderateThread(posts);
        } catch (Exception e) {
            log.error("Failed while moderation", e);
        }
        return CompletableFuture.completedFuture(null);
    }

    private void moderateThread(List<Post> posts) {
        posts.forEach((post)->{
            boolean hasBadWord = moderationDictionary.containsBadWord(post.getContent());
            boolean isToxic = !hasBadWord && aiModerationService.isToxic(post.getContent());

            post.setVerified(!(hasBadWord || isToxic));
            post.setVerifiedDate(LocalDateTime.now());
        });

        postRepository.saveAll(posts);
    }
}
