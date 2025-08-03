package faang.school.postservice.aspect;

import faang.school.postservice.dto.event.PostPublishingEvent;
import faang.school.postservice.model.Post;
import faang.school.postservice.publisher.PostPublishingEventPublisher;
import faang.school.postservice.repository.PostRepository;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.stream.IntStream;

@Component
@Slf4j
@Aspect
public class PostPublishingEventAspect {
    private final PostPublishingEventPublisher postPublishingEventPublisher;
    private final PostRepository postRepository;
    private final Executor postKafkaPublishExecutor;
    @Value("${spring.kafka.batch.subscribers}")
    private int batchSize;

    public PostPublishingEventAspect(PostPublishingEventPublisher postPublishingEventPublisher,
                                     PostRepository postRepository,
                                     @Qualifier("postKafkaPublishExecutor") Executor postKafkaPublishExecutor) {
        this.postPublishingEventPublisher = postPublishingEventPublisher;
        this.postRepository = postRepository;
        this.postKafkaPublishExecutor = postKafkaPublishExecutor;
    }

    @AfterReturning(
            value = "@annotation(faang.school.postservice.annotation.PostPublishingEventKafka)",
            returning = "result")
    public void publishPostPublishingEvent(Post result) {
        List<Long> subscribersList = postRepository.getAllFollowersOfPostAuthor(result.getAuthorId());
        int totalCount = subscribersList.size();
        log.info("publishPostPublishingEvent, there are {} subscribers for post {}", totalCount, result.getId());
        int totalBatches = (int) Math.ceil((double) totalCount / batchSize);

        IntStream.rangeClosed(1, totalBatches)
                .forEach(i -> postKafkaPublishExecutor.execute(() -> {
                    int firstIndex = batchSize * (i - 1);
                    int lastIndex = Math.min(batchSize * i, totalCount);
                    PostPublishingEvent event = PostPublishingEvent.builder()
                            .postId(result.getId())
                            .subscribers(subscribersList.subList(firstIndex, lastIndex))
                            .build();

                    postPublishingEventPublisher.publish(event);
                }));
    }
}
