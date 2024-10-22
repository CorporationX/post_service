package faang.school.postservice.service.impl;

import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.ModerationPostService;
import faang.school.postservice.util.ModerationDictionary;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ModerationPostServiceImpl implements ModerationPostService {

    private final PostRepository postRepository;
    private final ModerationDictionary moderationDictionary;

    @Override
    public void moderationPosts() {
        Map<Boolean, List<Post>> postsGrouped = groupPostsByVerified();

        CompletableFuture<Void> verifiedPostsFuture = asyncVerifyPosts(postsGrouped.get(true));
        CompletableFuture<Void> unverifiedPostsFuture = asyncVerifyBedPosts(postsGrouped.get(false));
        CompletableFuture.allOf(verifiedPostsFuture, unverifiedPostsFuture).join();

        asyncSaveChanges(postsGrouped.values());
    }

    @Async("verificationExecutor")
    public CompletableFuture<Void> asyncVerifyBedPosts(List<Post> posts) {
        Runnable asyncVerifyBedPosts = () -> posts.forEach(post -> post.setVerified(false));
        return runAsync(asyncVerifyBedPosts, "Error while processing unverified posts");
    }

    @Async("verificationExecutor")
    public CompletableFuture<Void> asyncVerifyPosts(List<Post> posts) {
        Runnable asyncVerifyPosts = () -> posts.forEach(post -> {
            post.setVerified(true);
            post.setVerifiedDate(LocalDateTime.now());
        });
        return runAsync(asyncVerifyPosts, "Error while processing verified posts");
    }

    @Async("verificationExecutor")
    public void asyncSaveChanges(Collection<List<Post>> posts) {
        Runnable saveChanges = () -> {
            List<Post> modifiedPosts = posts.stream()
                    .flatMap(Collection::stream)
                    .toList();
            postRepository.saveAll(modifiedPosts);
        };
        runAsync(saveChanges, "Error while saving posts");
    }

    private CompletableFuture<Void> runAsync(Runnable task, String errorMessage) {
        try {
            task.run();
        } catch (Exception ex) {
            log.error("{}: {}", errorMessage, ex.getMessage(), ex);
            throw new RuntimeException(errorMessage, ex);
        }
        return CompletableFuture.completedFuture(null);
    }

    private Map<Boolean, List<Post>> groupPostsByVerified() {
        return postRepository.findAll()
                .parallelStream()
                .collect(Collectors.partitioningBy(moderationDictionary::isVerified));
    }
}
