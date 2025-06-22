package faang.school.postservice.moderation.model;

import faang.school.postservice.config.moderation.CommentsModerationConfiguration;
import faang.school.postservice.config.moderation.ModerationDictionary;
import faang.school.postservice.model.Post;
import faang.school.postservice.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static java.util.stream.Collectors.*;

@Component
@Slf4j
@RequiredArgsConstructor
public class ModerationScheduled {

    private final PostService postService;
    private final ModerationDictionary moderationDictionary;
    private final ThreadPoolTaskExecutor executor;
    private final CommentsModerationConfiguration configuration;

    @Scheduled(cron = "#{@commentsModerationConfiguration.cron}")
    @Transactional
    public void startExecutor() {
        List<List<Post>> postList = verifiedPost();

        postList.forEach(post -> executor.submit(()-> checkContent(post)));
    }

    public List<Post> checkContent(List<Post> postList) {
        log.info("We start checking posts");
        return postList.stream()
                .peek(post -> {
                    String content = post.getContent();
                    boolean containsBadWord = moderationDictionary.containsProfanity(content);
                    if (!containsBadWord) {
                        post.setVerified(true);
                        post.setVerifiedDate(LocalDateTime.now());
                    }
                    log.info("Post verification completed");
                }).toList();
    }

    public List<List<Post>> verifiedPost() {
        List<Post> postList = postService.getAllPost();
        int batchSize = configuration.getBatchSize();

        List<Post> notVerified = postList.stream()
                .filter(post -> post.getVerifiedDate() == null)
                .collect(toList());

        return ListUtils.partition(notVerified, batchSize);
    }

}
