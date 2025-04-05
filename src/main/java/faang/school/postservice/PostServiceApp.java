package faang.school.postservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.Banner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.scheduling.annotation.EnableScheduling;

@Slf4j
@SpringBootApplication
@EnableScheduling
@EnableFeignClients(basePackages = "faang.school.postservice.client")
@ConfigurationPropertiesScan
public class PostServiceApp {
    public static void main(String[] args) {
        new SpringApplicationBuilder(PostServiceApp.class)
                .bannerMode(Banner.Mode.OFF)
                .run(args);
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        return objectMapper;
    }

    @Bean
    public KafkaAdmin.NewTopics topics() {
        log.info("Creation of Kafka topics");
        return new KafkaAdmin.NewTopics(
                TopicBuilder.name("posts").partitions(1).replicas(1).build()
        );
    }
}
