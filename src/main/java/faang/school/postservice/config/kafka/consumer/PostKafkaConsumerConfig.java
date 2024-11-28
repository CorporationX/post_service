package faang.school.postservice.config.kafka.consumer;

import faang.school.postservice.dto.kafka.event.PostEventDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;

@Configuration
@EnableKafka
public class PostKafkaConsumerConfig extends AbstractKafkaConsumerConfig<PostEventDto> {
    public PostKafkaConsumerConfig(@Value("${spring.data.kafka.consumer.post-consumer.group-id}") String groupId) {
        super(groupId, PostEventDto.class);
    }

    @Override
    @Bean("kafkaPostConsumerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, PostEventDto> getKafkaListenerContainerFactory() {
        return kafkaListenerContainerFactory();
    }
}
