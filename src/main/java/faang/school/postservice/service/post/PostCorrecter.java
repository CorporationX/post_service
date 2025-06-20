package faang.school.postservice.service.post;

import faang.school.postservice.dto.languagetool.LanguageToolResponse;
import faang.school.postservice.model.Post;
import faang.school.postservice.service.PostService;
import faang.school.postservice.service.languagetool.LanguageToolService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostCorrecter {
    private final PostService postService;
    private final LanguageToolService languageToolService;

    @Async("customExecutor")
    public CompletableFuture<Void> correctingContentPost(Post post) {
        String content = post.getContent();

        return languageToolService.checkText(content)
                .thenAccept(response -> {
                    String correctedContent = applyCorrected(content, response);
                    postService.updateCorrectedContentOfPost(post, correctedContent);
                }).exceptionally(ex -> {
                    log.error("Error correcting post with id: {}", post.getId(), ex);
                    return null;
                });
    }

    private String applyCorrected(String originalText, LanguageToolResponse response) {
        List<LanguageToolResponse.Match> matches = response.getMatches();
        if (matches.isEmpty()) {
            return originalText;
        }

        StringBuilder text = new StringBuilder(originalText);
        matches.sort(Comparator.comparingInt(LanguageToolResponse.Match::getOffset).reversed());

        for (LanguageToolResponse.Match match : matches) {
            String replacement = match.getReplacements().get(0).getValue();
            int offset = match.getOffset();
            int length = match.getLength();

            text.replace(offset, offset + length, replacement);
        }
        String correctedText = text.toString();
        log.info("applying text adjustments. original: {}, corrected: {}", originalText, correctedText);
        return correctedText;
    }
}