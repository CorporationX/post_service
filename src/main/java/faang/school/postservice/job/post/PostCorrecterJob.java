package faang.school.postservice.job.post;

import faang.school.postservice.client.LanguageToolClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.post.LanguageToolClientResponseDto;
import faang.school.postservice.model.post.Post;
import faang.school.postservice.service.post.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class PostCorrecterJob {
    private final PostService postService;
    private final LanguageToolClient languageToolClient;
    private final UserContext userContext;

    @Scheduled(cron = "${jobs.spellcheck.cron}")
    public void correctDraftPosts() {
        log.info("Starting spellcheck job for draft posts");
        userContext.setSystemUserId();

        List<Post> posts = postService.getAllDraftPosts();

        for (Post post : posts) {
            try {
                LanguageToolClientResponseDto response =
                        languageToolClient.checkSpelling(post.getContent(), post.getLanguage().toString());
                String correctedText = applyCorrections(post.getContent(), response.getMatches());

                post.setContent(correctedText);
                postService.updatePost(post);

                log.debug("Post with id {} corrected", post.getId());
            } catch (Exception ex) {
                log.warn("Failed to correct post with ID {}: {}", post.getId(), ex.getMessage(), ex);
            }
        }

        userContext.clear();
        log.info("Finished spellcheck job");
    }

    private String applyCorrections(String originalText, List<LanguageToolClientResponseDto.Match> matches) {
        matches.sort(Comparator.comparingInt(LanguageToolClientResponseDto.Match::getOffset).reversed());

        StringBuilder sb = new StringBuilder(originalText);
        for (LanguageToolClientResponseDto.Match match : matches) {
            if (!match.getReplacements().isEmpty()) {
                String replacement = match.getReplacements().get(0).getValue();
                int offset = match.getOffset();
                int length = match.getLength();
                sb.replace(offset, offset + length, replacement);
            }
        }

        return sb.toString();
    }
}
