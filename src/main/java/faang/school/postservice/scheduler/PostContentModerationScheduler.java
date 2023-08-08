package faang.school.postservice.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PostContentModerationScheduler {
    // Тут пока просто проверял, оно работает)
    private int i = 0;

    @Scheduled(cron = "${post.publisher.scheduler.cron}")
    @Async("taskExecutor")
    public void de() {
        System.out.println(i++);
        System.out.println(Thread.currentThread().getId());
    }
}
