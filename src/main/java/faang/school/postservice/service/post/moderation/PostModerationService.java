package faang.school.postservice.service.post.moderation;

import faang.school.postservice.config.moderation.ModerationDictionary;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class PostModerationService {

    private final PostRepository postRepository;
    private final ModerationDictionary moderationDictionary;

    @Transactional
    public void verifyPostsBatch(int batchSize) {
        List<Post> postsBatch = postRepository.getNotVerifiedPostsLimitedWithLock(batchSize);
        log.info("{} posts locked for moderation", postsBatch.size());

        postsBatch.forEach(post -> {
            boolean containsBadWord = moderationDictionary.containsProfanity(post.getContent());
            post.setVerified(!containsBadWord);
            post.setVerifiedDate(LocalDateTime.now());
        });

        log.info("{} not verified posts successfully processed", postsBatch.size());
        postRepository.saveAll(postsBatch);
    }

    @Transactional(readOnly = true)
    public int countNotVerifiedPosts() {
        return postRepository.countNotVerifiedPosts();
    }
}
