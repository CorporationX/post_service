package faang.school.postservice.jobs;

import faang.school.postservice.model.Post;
import faang.school.postservice.service.PostService;
import faang.school.postservice.service.post.PostCorrecter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class CorrectorUnpublishPosts {

    private final PostCorrecter postCorrecter;
    private final PostService postService;

    @Scheduled(cron = "${app.correction.cron}")
    public void correctingSpellingOfPosts() {
        log.info("Starting spellcheck job for draft posts");

        List<Post> posts = postService.getAllUnpublishedPost();
        List<CompletableFuture<Void>> futures = posts.stream()
                .map(postCorrecter::correctingContentPost)
                .toList();

        CompletableFuture<Void> allFutures = CompletableFuture.allOf(
                futures.toArray(new CompletableFuture[0])
        );

        allFutures.join();
        log.info("Finished spellcheck job");
    }
}
