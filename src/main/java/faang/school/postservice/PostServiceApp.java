package faang.school.postservice;

import faang.school.postservice.config.kafka.properties.AsyncProperties;
import faang.school.postservice.config.kafka.properties.BatchProperties;
import faang.school.postservice.config.kafka.properties.RetryProperties;
import faang.school.postservice.config.kafka.properties.ThreadPoolProperties;
import org.springframework.boot.Banner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableFeignClients(basePackages = "faang.school.postservice.client")
@EnableRetry
@EnableConfigurationProperties({AsyncProperties.class, BatchProperties.class, RetryProperties.class, ThreadPoolProperties.class})
public class PostServiceApp {
    public static void main(String[] args) {
        new SpringApplicationBuilder(PostServiceApp.class)
                .bannerMode(Banner.Mode.OFF)
                .run(args);
    }
}
