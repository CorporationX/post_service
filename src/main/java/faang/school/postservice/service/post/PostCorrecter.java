package faang.school.postservice.service.post;

import faang.school.postservice.dto.languagetool.LanguageToolResponse;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.PostService;
import faang.school.postservice.service.languagetool.LanguageToolService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class PostCorrecter {
    private final PostService postService;
    private final LanguageToolService languageToolService;
    private final PostRepository postRepository;

    @Value("${app.correction.language}")
    private String language;

    @Scheduled(cron = "${app.correction.cron}")
    public void correctingSpellingOfPosts() {
        log.info("Starting spellcheck job for draft posts");

        List<Post> posts = postService.getAllUnpublishedPost();
        List<CompletableFuture<Void>> futures = posts.stream()
                .map(this::correctingContentPost)
                .toList();
        CompletableFuture<Void> allFutures = CompletableFuture.allOf(
                futures.toArray(new CompletableFuture[0])
        );

        allFutures.join();
        log.info("Finished spellcheck job");
    }

    @Async("customExecutor")
    public CompletableFuture<Void> correctingContentPost(Post post) {
        String content = post.getContent();

        return languageToolService.checkText(content, language)
                .thenAccept(response -> {
                    String correctedContent = applyCorrected(content, response);
                    post.setContent(correctedContent);
                    postRepository.save(post);
                    log.info("post text ID: {} updated", post.getId());
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
