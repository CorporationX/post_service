package faang.school.postservice.config.kafka.consumer;

import faang.school.postservice.dto.kafka.event.LikeEventDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;

@Configuration
public class LikeKafkaConsumerConfig extends AbstractKafkaConsumerConfig<LikeEventDto> {

    public LikeKafkaConsumerConfig(@Value("${spring.data.kafka.consumer.like-consumer.group-id}") String groupId) {
        super(groupId, LikeEventDto.class);
    }

    @Override
    @Bean("kafkaLikeConsumerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, LikeEventDto> getKafkaListenerContainerFactory() {
        return kafkaListenerContainerFactory();
    }
}
