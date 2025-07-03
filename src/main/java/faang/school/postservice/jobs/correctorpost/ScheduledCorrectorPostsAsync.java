package faang.school.postservice.jobs.correctorpost;

import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.post.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.stream.LongStream;

@Component
@Slf4j
@RequiredArgsConstructor
public class ScheduledCorrectorPostsAsync {

    private final PostService postService;
    private final PostRepository postRepository;
    @Value("${app.correction.batch-size}")
    private int batchSize;

    @Async("executorForPostService")
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
