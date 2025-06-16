package faang.school.postservice.job.post;

import faang.school.postservice.client.language_tool.LanguageToolClient;
import faang.school.postservice.dto.post.LanguageToolClientResponseDto;
import faang.school.postservice.entity.post.Post;
import faang.school.postservice.service.post.PostService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Executor;

@Component
@Slf4j
public class PostCorrecterJob {
    private static final int NUM_THREADS = 10;
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
            String correctedText = applyCorrections(post.getContent(), response.getMatches());

            post.setContent(correctedText);
            postService.updatePost(post);

            log.debug("Post with id {} corrected", post.getId());
        } catch (Exception ex) {
            log.warn("Failed to correct post with ID {}: {}", post.getId(), ex.getMessage(), ex);
        }
    }

    private String applyCorrections(String originalText, List<LanguageToolClientResponseDto.Match> matches) {
        matches.sort(Comparator.comparingInt(LanguageToolClientResponseDto.Match::getOffset).reversed());

        StringBuilder sb = new StringBuilder(originalText);
        for (LanguageToolClientResponseDto.Match match : matches) {
            if (!match.getReplacements().isEmpty()) {
                String replacement = match.getReplacements().get(0).getValue();
                int offset = match.getOffset();
                int length = match.getLength();

                String originalFragment =
                        originalText.substring(offset, Math.min(offset + length, originalText.length()));
                log.debug("Correction: replacing '{}' with '{}' at offset {} (length {})",
                        originalFragment, replacement, offset, length);
                sb.replace(offset, offset + length, replacement);
            }
        }
        return sb.toString();
    }
}
