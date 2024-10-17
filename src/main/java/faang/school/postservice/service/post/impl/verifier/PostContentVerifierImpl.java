package faang.school.postservice.service.post.impl.verifier;

import faang.school.postservice.dictionary.ModerationDictionary;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.post.PostContentVerifier;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class PostContentVerifierImpl implements PostContentVerifier {

    private final ModerationDictionary moderationDictionary;
    private final PostRepository postRepository;

    @Override
    @Async("taskExecutor")
    public void verifyPosts(List<Post> posts) {
        Set<String> banWords = moderationDictionary.getForbiddenWords();
        for (Post post : posts) {
            boolean containsBanWord = banWords.stream()
                    .anyMatch(banWord -> post.getContent().toLowerCase().contains(banWord));
            if (containsBanWord) {
                post.setVerified(false);
                log.warn("Post '{}' contains forbidden words. It will not be verified.", post.getId());
            } else {
                post.setVerified(true);
                log.info("Post '{}' has been successfully verified.",
                        post.getId());
            }
            post.setVerifiedDate(LocalDateTime.now());
        }
        postRepository.saveAll(posts);
    }
}
