package faang.school.postservice.job.post;

import faang.school.postservice.client.language_tool.LanguageToolClient;
import faang.school.postservice.dto.post.LanguageToolClientResponseDto;
import faang.school.postservice.entity.post.Post;
import faang.school.postservice.service.post.PostService;
import faang.school.postservice.utils.string.LanguageToolUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.Executor;

@Component
@Slf4j
public class PostCorrecterJob {

    private final PostService postService;
    private final LanguageToolClient languageToolClient;
    private final Executor executor;

    public PostCorrecterJob(PostService postService,
                            LanguageToolClient languageToolClient,
                            @Qualifier("correctDraftPosts") Executor executor) {
        this.postService = postService;
        this.languageToolClient = languageToolClient;
        this.executor = executor;
    }

    @Scheduled(cron = "${jobs.spellcheck.cron}")
    public void correctDraftPosts() {

        log.info("Starting spellcheck job for draft posts");

        List<Post> posts = postService.getAllDraftPosts();

        posts.forEach(post -> executor.execute(() -> correctAndSavePost(post)));

        log.info("Finished spellcheck job");
    }

    private void correctAndSavePost(Post post) {
        try {
            LanguageToolClientResponseDto response =
                    languageToolClient.checkSpelling(post.getContent(), "auto");
            String correctedText = LanguageToolUtils.applyCorrections(post.getContent(), response.getMatches());

            post.setContent(correctedText);
            postService.updatePost(post);

            log.debug("Post with id {} corrected", post.getId());
        } catch (Exception ex) {
            log.warn("Failed to correct post with ID {}: {}", post.getId(), ex.getMessage(), ex);
        }
    }
}
