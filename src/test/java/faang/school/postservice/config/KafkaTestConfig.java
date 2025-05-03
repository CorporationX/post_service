package faang.school.postservice.config;

import faang.school.postservice.dto.post.PostEventDto;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.utils.KafkaTestUtils;

import java.util.Map;

@TestConfiguration
public class KafkaTestConfig {

    @Bean
    public ConsumerFactory<String, PostEventDto> postEventConsumerFactory(EmbeddedKafkaBroker broker) {
        Map<String, Object> configs = KafkaTestUtils.consumerProps("${spring.kafka.consumer.group-id.posts:posts-group}", "true", broker);
        return new DefaultKafkaConsumerFactory<>(
                configs,
                new StringDeserializer(),
                new JsonDeserializer<>(PostEventDto.class)
        );
    }
}

