package faang.school.postservice.config.async;

import faang.school.postservice.config.context.UserContext;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class ExecutorConfig {
    private final UserContext userContext;

    @Autowired
    private ApplicationContext context;

    @Value("${spring.scheduler.post.publisher.threads-count}")
    private int threadsCountForPostPublisher;

    @Value("${spring.scheduler.comment.moderator.treads-count}")
    private int threadsCountCommentModerator;

    @Bean(name = "executorCommentModerator")
    public ExecutorService executorCommentModerator() {
        return Executors.newFixedThreadPool(threadsCountCommentModerator);
    }

    @Bean(name = "executorPostPublisher")
    public ExecutorService executorPostPublisher() {
        return Executors.newFixedThreadPool(threadsCountForPostPublisher);
    }

    @Bean(name = "cacheWarmerExecutor")
    public ExecutorService cacheWarmerExecutor() {
        return Executors.newFixedThreadPool(3, r -> {
            Thread thread = new Thread(r);
            thread.setContextClassLoader(this.getClass().getClassLoader());
            userContext.getUserId();
            thread.setUncaughtExceptionHandler((t, e) ->
                    log.error("Uncaught exception in thread: {}", t.getName(), e));
            return thread;
        });
    }

    @PreDestroy
    public void cleanup() {
        Map<String, ExecutorService> executors = context.getBeansOfType(ExecutorService.class);

        for (Map.Entry<String, ExecutorService> entry : executors.entrySet()) {
            String name = entry.getKey();
            ExecutorService executor = entry.getValue();

            log.info("Shutting down executor: {}", name);
            if (executor != null) {
                executor.shutdown();
                try {
                    if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                        executor.shutdownNow();
                    }
                } catch (InterruptedException e) {
                    executor.shutdownNow();
                    Thread.currentThread().interrupt();
                }
            }
        }
    }
}