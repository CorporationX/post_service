package faang.school.postservice.service.post;

import faang.school.postservice.config.ModerationProperties;
import faang.school.postservice.dictionary.ModerationDictionary;
import faang.school.postservice.model.Post;
import faang.school.postservice.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

@Component
@Slf4j
@RequiredArgsConstructor
public class ContentModerator {
    private final PostService postService;
    private final ModerationDictionary moderationDictionary;
    private final Executor moderationExecutor;
    private final ModerationProperties moderationProperties;

    public void moderate() {
        log.info("Post content moderation has started " + Thread.currentThread().getId() + " " + LocalDateTime.now());
        var posts = postService.getAllPosts().stream()
                .filter(post ->
                        post.getVerifiedDate() == null || checkTime(post.getVerifiedDate()) ||
                                (post.getUpdatedAt() != null && post.getUpdatedAt().minusSeconds(2).isAfter(post.getVerifiedDate()))
                )
                .toList();
        for (int i = 0; i < posts.size(); i += moderationProperties.getBatchSize()) {
            List<Post> postBatch = posts.subList(i, Math.min(posts.size(), i + moderationProperties.getBatchSize()));
            moderationExecutor.execute(() -> {
                moderatePosts(postBatch);
            });
        }
        log.info("Post content moderation is done " + Thread.currentThread().getId() + " " + LocalDateTime.now());
    }

    @Async("moderationExecutor")
    public void moderateComment() {
        log.info("Comment content moderation has started " + Thread.currentThread().getId() + " " + LocalDateTime.now());
        List<Post> allPosts = postService.getAllPosts();
        List<Post> VerifiedPosts = new ArrayList<>();
        for (Post allPost : allPosts) {
            if (checkTime(allPost.getVerifiedDate())) {
                VerifiedPosts.add(allPost);
            }
        }
        moderatePosts(VerifiedPosts);
        log.info("Comment content moderation is done " + Thread.currentThread().getId() + " " + LocalDateTime.now());
    }

    private void moderatePosts(List<Post> posts) {
        posts.forEach(post -> {
            post.setVerified(!moderationDictionary.containsCensorWord(post.getContent()));
            post.setVerifiedDate(LocalDateTime.now());
            postService.save(post);
        });
    }

    private boolean checkTime(LocalDateTime time) {
        return ChronoUnit.SECONDS.between(time, LocalDateTime.now()) > moderationProperties.getSecondsBetweenModeration();
    }
}
