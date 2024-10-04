package faang.school.postservice.service.post;

import faang.school.postservice.model.Post;
import faang.school.postservice.model.VerificationPostStatus;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ModerationProcessingService {

    private final PostRepository postRepository;
    private final ModerationDictionary moderationDictionary;

    @Transactional
    public void moderatePostsSublist(List<Post> posts) {
        List<Post> updatedPosts = posts.stream().map(post -> {
            if (moderationDictionary.containsForbiddenWord(post.getContent())) {
                post.setVerificationStatus(VerificationPostStatus.REJECTED);
            } else {
                post.setVerificationStatus(VerificationPostStatus.VERIFIED);
            }
            post.setVerifiedDate(LocalDateTime.now());
            return post;
        }).collect(Collectors.toList());

        postRepository.saveAll(updatedPosts);
        log.info("Сохранено постов: {}", updatedPosts.size());
    }
}
