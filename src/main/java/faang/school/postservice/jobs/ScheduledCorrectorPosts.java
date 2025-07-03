package faang.school.postservice.jobs;

import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.PostService;
import faang.school.postservice.service.post.PostCorrecter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.stream.LongStream;

@Slf4j
@Component
@RequiredArgsConstructor
public class ScheduledCorrectorPosts {

    private final PostService postService;
    private final PostRepository postRepository;
    @Value("${app.correction.batch-size}")
    private int batchSize;

    @Async("executorForPostService")
    @Scheduled(cron = "${app.correction.cron}")
    public void correctingSpellingOfPosts() {
        log.info("Scheduled job correcter post content started");
        long count = postRepository.countDraftPosts();
        long batches = (count + batchSize - 1) / batchSize;
        log.debug("count draft posts: {}, batches: {}", count, batches);

        if (count == 0) {
            log.info("No draft posts found for correction, skipping jobs.");
            return;
        }

        LongStream.range(0, batches).forEach(i -> {
            postService.correctingContentBatchPostsAsync(batchSize);
            log.info("starting asynchronous patch processing under index {},", i);
        });
    }
}