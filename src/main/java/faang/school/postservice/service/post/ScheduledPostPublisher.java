package faang.school.postservice.service.post;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.stream.IntStream;

@Slf4j
@RequiredArgsConstructor
@Component
public class ScheduledPostPublisher {
    @Value("${post.publisher.batch-size}")
    private Integer postPublishBatchSize;

    private final PostService postService;

    @Scheduled(cron = "${post.publisher.scheduler.cron}")
    public void scheduledPostPublish() {
        log.info("Запуск процесса публикации постов по расписанию");
        int readyToPublishCount = postService.getReadyToPublish();
        int countOfTasks = countOfTasks(readyToPublishCount);
        IntStream.range(0, countOfTasks)
                .forEach(i -> postService.processReadyToPublishPosts(postPublishBatchSize));
    }

    private int countOfTasks(int postCount) {
        return postCount % postPublishBatchSize == 0 ? postCount / postPublishBatchSize
                : (postCount / postPublishBatchSize) + 1;
    }
}
