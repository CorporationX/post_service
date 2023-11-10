package faang.school.postservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
public class NewsFeedThreadPoolConfig {

    @Value("${spring.data.kafka.async.thread-pool-settings.post.core-pool-size}")
    private int postCorePoolSize;
    @Value("${spring.data.kafka.async.thread-pool-settings.post.max-pool-size}")
    private int postMaxPoolSize;
    @Value("${spring.data.kafka.async.thread-pool-settings.post.queue-capacity}")
    private int postQueueCapacity;

    @Value("${spring.data.kafka.async.thread-pool-settings.post-view.core-pool-size}")
    private int postViewsCorePoolSize;
    @Value("${spring.data.kafka.async.thread-pool-settings.post-view.max-pool-size}")
    private int postViewsMaxPoolSize;
    @Value("${spring.data.kafka.async.thread-pool-settings.post-view.queue-capacity}")
    private int postViewsQueueCapacity;

    @Value("${spring.data.kafka.async.thread-pool-settings.comments.core-pool-size}")
    private int commentsCorePoolSize;
    @Value("${spring.data.kafka.async.thread-pool-settings.comments.max-pool-size}")
    private int commentsMaxPoolSize;
    @Value("${spring.data.kafka.async.thread-pool-settings.comments.queue-capacity}")
    private int commentsQueueCapacity;

    @Value("${spring.data.kafka.async.thread-pool-settings.likes.core-pool-size}")
    private int likesCorePoolSize;
    @Value("${spring.data.kafka.async.thread-pool-settings.likes.max-pool-size}")
    private int likesMaxPoolSize;
    @Value("${spring.data.kafka.async.thread-pool-settings.likes.queue-capacity}")
    private int likesQueueCapacity;

    @Value("${spring.data.feed.async.core-pool-size}")
    private int feedCorePoolSize;
    @Value("${spring.data.feed.async.max-pool-size}")
    private int feedMaxPoolSize;
    @Value("${spring.data.feed.async.queue-capacity}")
    private int feedQueueCapacity;

    @Value("${spring.data.feed.heater.async.core-pool-size}")
    private int heaterCorePoolSize;
    @Value("${spring.data.feed.heater.async.max-pool-size}")
    private int heaterMaxPoolSize;
    @Value("${spring.data.feed.heater.async.queue-capacity}")
    private int heaterQueueCapacity;


    @Bean
    public ThreadPoolTaskExecutor postEventTaskExecutor() {
        return buildCustomTaskThreadPool(postCorePoolSize, postMaxPoolSize, postQueueCapacity);
    }

    @Bean
    public ThreadPoolTaskExecutor postViewsTaskExecutor() {
        return buildCustomTaskThreadPool(postViewsCorePoolSize, postMaxPoolSize, postQueueCapacity);
    }

    @Bean
    public ThreadPoolTaskExecutor commentTaskExecutor() {
        return buildCustomTaskThreadPool(commentsCorePoolSize, commentsMaxPoolSize, commentsQueueCapacity);
    }

    @Bean
    public ThreadPoolTaskExecutor likePostTaskExecutor() {
        return buildCustomTaskThreadPool(likesCorePoolSize, likesMaxPoolSize, likesQueueCapacity);
    }

    @Bean
    public ThreadPoolTaskExecutor likeCommentTaskExecutor() {
        return buildCustomTaskThreadPool(likesCorePoolSize, likesMaxPoolSize, likesQueueCapacity);
    }

    @Bean
    public ThreadPoolTaskExecutor feedTaskExecutor() {
        return buildCustomTaskThreadPool(feedCorePoolSize, feedMaxPoolSize, feedQueueCapacity);
    }

    @Bean
    public ThreadPoolTaskExecutor feedHeaterTaskExecutor() {
        return buildCustomTaskThreadPool(heaterCorePoolSize, heaterMaxPoolSize, heaterQueueCapacity);
    }

    private ThreadPoolTaskExecutor buildCustomTaskThreadPool(int corePoolSize, int maxPoolSize, int queueCapacity) {
        ThreadPoolTaskExecutor likeTaskExecutor = new ThreadPoolTaskExecutor();

        likeTaskExecutor.setCorePoolSize(likesCorePoolSize);
        likeTaskExecutor.setMaxPoolSize(likesMaxPoolSize);
        likeTaskExecutor.setQueueCapacity(likesQueueCapacity);

        return likeTaskExecutor;
    }
}