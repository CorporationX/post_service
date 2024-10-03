package faang.school.postservice.service.post;

import faang.school.postservice.model.Post;
import faang.school.postservice.model.VerificationPostStatus;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
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

    @Value("${moderation.sublist-size}")
    private int sublistSize;

    @Async
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
        log.info("Posts saved: {}", updatedPosts.size());
    }

    @Transactional
    public void moderateUnverifiedPosts() {
        List<Post> unverifiedPosts = postRepository.findUnverifiedPosts();
        List<List<Post>> sublists = splitListIntoSublists(unverifiedPosts, sublistSize);
        sublists.parallelStream().forEach(this::moderatePostsSublist);
    }

    private List<List<Post>> splitListIntoSublists(List<Post> posts, int sublistSize) {
        return IntStream.range(0, (posts.size() + sublistSize - 1) / sublistSize)
                .mapToObj(i -> posts.subList(i * sublistSize, Math.min(posts.size(), (i + 1) * sublistSize)))
                .collect(Collectors.toList());
    }
}