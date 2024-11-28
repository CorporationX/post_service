package faang.school.postservice.config.kafka.consumer;

import faang.school.postservice.dto.kafka.event.CommentEventDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;

@Configuration
public class CommentKafkaConsumerConfig extends AbstractKafkaConsumerConfig<CommentEventDto> {
    public CommentKafkaConsumerConfig(@Value("${spring.data.kafka.consumer.comment-consumer.group-id}") String groupId) {
        super(groupId, CommentEventDto.class);
    }

    @Override
    @Bean("kafkaCommentConsumerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, CommentEventDto> getKafkaListenerContainerFactory() {
        return kafkaListenerContainerFactory();
    }
}
