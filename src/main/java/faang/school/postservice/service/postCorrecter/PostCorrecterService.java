package faang.school.postservice.service.postCorrecter;

import com.fasterxml.jackson.core.JsonProcessingException;
import faang.school.postservice.config.BingSpellCheckingConfig.BingSpellCheckingConfig;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostCorrecterService {

    private final PostRepository postRepository;
    private final TextCorrecter textCorrecter;
    private final BingSpellCheckingConfig bingSpellCheckingConfig;
    private final ThreadPoolTaskExecutor threadPoolTaskExecutor;

    public void correctUnpublishedPosts() throws JsonProcessingException {
        List<Post> readyToPublish = postRepository.findNotPublished();

        int rateLimit = bingSpellCheckingConfig.getRateLimitPerSecond();
        AtomicInteger requestCount = new AtomicInteger();
        threadPoolTaskExecutor.submit(() -> {
            for (Post toPublish : readyToPublish) {
                String content = toPublish.getContent();
                Post post = toPublish;
                try {
                    if (requestCount.get() >= rateLimit) {
                        Thread.sleep(1000);
                        requestCount.set(0);
                    }
                    post.setContent(textCorrecter.getCorrectText(content).get());
                    requestCount.getAndIncrement();
                    post.setCorrected(true);
                    log.info("Draft posts was corrected successfully postId ={}", toPublish.getId());
                } catch (JsonProcessingException | ExecutionException | InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            postRepository.saveAll(readyToPublish);
        });
    }
}
