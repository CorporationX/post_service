package faang.school.postservice;

import faang.school.postservice.config.ModerationProperties;
import faang.school.postservice.properties.KafkaProperties;
import faang.school.postservice.properties.RedisProperties;
import faang.school.postservice.properties.feed.FeedCacheProperties;
import faang.school.postservice.properties.feed.FeedExecutorProperties;
import faang.school.postservice.properties.feed.FeedHeaterExecutorProperties;
import faang.school.postservice.properties.feed.FeedHeaterProperties;
import org.springframework.boot.Banner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableAsync
@EnableRetry
@EnableKafka
@EnableFeignClients(basePackages = "faang.school.postservice.client")
@EnableConfigurationProperties({
        FeedExecutorProperties.class,
        FeedHeaterExecutorProperties.class,
        ModerationProperties.class,
        FeedCacheProperties.class,
        KafkaProperties.class,
        FeedHeaterProperties.class,
        RedisProperties.class
})
public class PostServiceApp {
    public static void main(String[] args) {
        new SpringApplicationBuilder(PostServiceApp.class)
                .bannerMode(Banner.Mode.OFF)
                .run(args);
    }
}
