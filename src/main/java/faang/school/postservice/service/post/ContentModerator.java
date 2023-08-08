package faang.school.postservice.service.post;

import faang.school.postservice.config.AsyncConfig;
import faang.school.postservice.dictionary.ModerationDictionary;
import faang.school.postservice.model.Post;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
@Slf4j
public class ContentModerator {
    private final PostService postService;
    private final ModerationDictionary moderationDictionary;
    private final int secondsBetweenModeration;
    private final int batchSize;
    private final AsyncConfig asyncConfig;

    @Autowired
    public ContentModerator(@Value("${ContentModerator.secondsBetweenModeration}") int secondsBetweenModeration, @Value("${ContentModerator.batchSize}") int batchSize, PostService postService, ModerationDictionary moderationDictionary, AsyncConfig asyncConfig) {
        this.secondsBetweenModeration = secondsBetweenModeration;
        this.batchSize = batchSize;
        this.postService = postService;
        this.moderationDictionary = moderationDictionary;
        this.asyncConfig = asyncConfig;
    }

    public void moderate() {
        var posts = postService.getAllPosts().stream()
                .filter(post ->
                        ChronoUnit.SECONDS.between(post.getVerifiedDate(), LocalDateTime.now()) > secondsBetweenModeration)
                .toList();
        if (posts.isEmpty()) return;

        var executor = asyncConfig.taskExecutor();
        for (int i = 0; i < posts.size(); i += batchSize) {
            List<Post> postBatch = posts.subList(i, Math.min(posts.size(), i + batchSize));
            executor.execute(() -> {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                moderatePosts(postBatch);
                log.info(Thread.currentThread().getName()); // Тестил, что работает
            });
            System.out.println(i);
        }
    }

    private void moderatePosts(List<Post> posts) {
        posts.forEach(post -> {
            post.setVerified(!moderationDictionary.containsCensorWord(post.getContent()));
            post.setVerifiedDate(LocalDateTime.now());
            postService.save(post);
        });
    }
}
