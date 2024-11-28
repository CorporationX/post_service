package faang.school.postservice.config.kafka.consumer;

import faang.school.postservice.dto.kafka.event.PostViewEventDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;

@Configuration
public class PostViewKafkaConsumerConfig extends AbstractKafkaConsumerConfig<PostViewEventDto> {
    public PostViewKafkaConsumerConfig(@Value("${spring.data.kafka.consumer.post-view-consumer.group-id}") String groupId) {
        super(groupId, PostViewEventDto.class);
    }

    @Override
    @Bean("kafkaPostViewConsumerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, PostViewEventDto> getKafkaListenerContainerFactory() {
        return kafkaListenerContainerFactory();
    }
}
