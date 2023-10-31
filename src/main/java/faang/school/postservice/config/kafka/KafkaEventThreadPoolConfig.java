package faang.school.postservice.config.kafka;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class KafkaEventThreadPoolConfig {

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



    @Bean("postEventTaskExecutor")
    public ThreadPoolTaskExecutor postEventTaskExecutor() {
        ThreadPoolTaskExecutor postEventTaskExecutor = new ThreadPoolTaskExecutor();

        postEventTaskExecutor.setCorePoolSize(postCorePoolSize);
        postEventTaskExecutor.setMaxPoolSize(postMaxPoolSize);
        postEventTaskExecutor.setQueueCapacity(postQueueCapacity);

        return postEventTaskExecutor;
    }

    @Bean("postViewsTaskExecutor")
    public ThreadPoolTaskExecutor postViewsTaskExecutor() {
        ThreadPoolTaskExecutor postViewsTaskExecutor = new ThreadPoolTaskExecutor();

        postViewsTaskExecutor.setCorePoolSize(postViewsCorePoolSize);
        postViewsTaskExecutor.setMaxPoolSize(postViewsMaxPoolSize);
        postViewsTaskExecutor.setQueueCapacity(postViewsQueueCapacity);

        return postViewsTaskExecutor;
    }

    @Bean("commentTaskExecutor")
    public ThreadPoolTaskExecutor commentTaskExecutor() {
        ThreadPoolTaskExecutor commentTaskExecutor = new ThreadPoolTaskExecutor();

        commentTaskExecutor.setCorePoolSize(commentsCorePoolSize);
        commentTaskExecutor.setMaxPoolSize(commentsMaxPoolSize);
        commentTaskExecutor.setQueueCapacity(commentsQueueCapacity);

        return commentTaskExecutor;
    }

    @Bean("likeTaskExecutor")
    public ThreadPoolTaskExecutor likeTaskExecutor() {
        ThreadPoolTaskExecutor likeTaskExecutor = new ThreadPoolTaskExecutor();

        likeTaskExecutor.setCorePoolSize(likesCorePoolSize);
        likeTaskExecutor.setMaxPoolSize(likesMaxPoolSize);
        likeTaskExecutor.setQueueCapacity(likesQueueCapacity);

        return likeTaskExecutor;
    }
}
