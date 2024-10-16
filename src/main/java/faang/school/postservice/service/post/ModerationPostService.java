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
import java.util.stream.IntStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class ModerationPostService {
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
        log.info("Saved {} posts", updatedPosts.size());
    }

    @Transactional(readOnly = true)
    public List<Post> findUnverifiedPosts() {
        return postRepository.findByVerificationStatus(VerificationPostStatus.UNVERIFIED);
    }

    public List<List<Post>> splitListIntoSublists(List<Post> posts, int sublistSize) {
        if (sublistSize <= 0) {
            throw new IllegalArgumentException("Sublist size must be greater than zero.");
        }
        return IntStream.range(0, (posts.size() + sublistSize - 1) / sublistSize)
                .mapToObj(i -> posts.subList(i * sublistSize, Math.min(posts.size(), (i + 1) * sublistSize)))
                .collect(Collectors.toList());
    }
}