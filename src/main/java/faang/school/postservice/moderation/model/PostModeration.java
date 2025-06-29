package faang.school.postservice.moderation.model;

import faang.school.postservice.config.moderation.CommentsModerationConfiguration;
import faang.school.postservice.config.moderation.ModerationDictionary;
import faang.school.postservice.model.Post;
import faang.school.postservice.service.PostService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.ListUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class PostModeration {

    private final PostService postService;
    private final ModerationDictionary moderationDictionary;
    private final ThreadPoolTaskExecutor executor;
    private final CommentsModerationConfiguration configuration;

    @Scheduled(cron = "#{@commentsModerationConfiguration.cron}")
    public void startExecutor() {
        List<List<Post>> postList = searchUnverifiedPosts();

        postList.forEach(batch -> executor.submit(() -> checkContent(batch)));
    }

    public void checkContent(List<Post> postList) {
        for (Post post : postList) {
            String content = post.getContent();
            boolean containsBadWord = moderationDictionary.containsProfanity(content);
            if (containsBadWord) {
                post.setVerified(false);
                post.setVerifiedDate(LocalDateTime.now());
            }
            post.setVerified(true);
            post.setVerifiedDate(LocalDateTime.now());
        }
    }

    @Transactional
    public List<List<Post>> searchUnverifiedPosts() {
        List<Post> postList = postService.getNotVerifiedPosts();
        int batchSize = configuration.getBatchSize();

        return ListUtils.partition(postList, batchSize);
    }

}
