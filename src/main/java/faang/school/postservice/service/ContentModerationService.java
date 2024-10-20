package faang.school.postservice.service;

import faang.school.postservice.exception.PostModerationException;
import faang.school.postservice.model.ModerationStatus;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.scheduler.post.moderation.AhoCorasickContentChecker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ContentModerationService {
    private final PostRepository postRepository;
    private final AhoCorasickContentChecker contentChecker;

    @Transactional
    public void checkContentAndModerate(List<Post> posts) throws PostModerationException {
        for (Post post : posts) {
            boolean hasBadContent = contentChecker.containsBadContent(post.getContent());

            if (hasBadContent) {
                post.setModerationStatus(ModerationStatus.REJECTED);
            } else {
                post.setModerationStatus(ModerationStatus.VERIFIED);
                post.setVerifiedAt(LocalDateTime.now());
            }
        }
        postRepository.saveAll(posts);
    }
}
