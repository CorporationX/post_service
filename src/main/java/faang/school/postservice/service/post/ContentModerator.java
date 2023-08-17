package faang.school.postservice.service.post;

import faang.school.postservice.dictionary.ModerationDictionary;
import faang.school.postservice.model.Post;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.Executor;

@Component
@Slf4j
public class ContentModerator {
    @Value("${ContentModerator.secondsBetweenModeration}")
    private int SECOND_BETWEEN_MODERATION;
    @Value("${ContentModerator.batchSize}")
    private int BATCH_SIZE;
    private final PostService postService;
    private final ModerationDictionary moderationDictionary;
    private final Executor executor;

    @Autowired
    public ContentModerator(
            PostService postService,
            ModerationDictionary moderationDictionary,
            @Qualifier("taskExecutor") Executor executor
    ) {
        this.postService = postService;
        this.moderationDictionary = moderationDictionary;
        this.executor = executor;
    }

    public void moderate() {
        log.info("Post content moderation has started " + Thread.currentThread().getId() + " " + LocalDateTime.now());
        var posts = postService.getAllPosts().stream()
                .filter(post ->
                        post.getVerifiedDate() == null || checkTime(post.getVerifiedDate()) ||
                                (post.getUpdatedAt() != null && post.getUpdatedAt().minusSeconds(2).isAfter(post.getVerifiedDate()))
                )
                .toList();
        for (int i = 0; i < posts.size(); i += BATCH_SIZE) {
            List<Post> postBatch = posts.subList(i, Math.min(posts.size(), i + BATCH_SIZE));
            executor.execute(() -> {
                moderatePosts(postBatch);
            });
        }
        log.info("Post content moderation is done " + Thread.currentThread().getId() + " " + LocalDateTime.now());
    }

    private void moderatePosts(List<Post> posts) {
        posts.forEach(post -> {
            post.setVerified(!moderationDictionary.containsCensorWord(post.getContent()));
            post.setVerifiedDate(LocalDateTime.now());
            postService.save(post);
        });
    }

    private boolean checkTime(LocalDateTime time) {
        return ChronoUnit.SECONDS.between(time, LocalDateTime.now()) > SECOND_BETWEEN_MODERATION;
    }
}
