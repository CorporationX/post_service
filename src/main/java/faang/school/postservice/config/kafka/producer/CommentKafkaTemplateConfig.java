package faang.school.postservice.config.kafka.producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.kafka.event.CommentEventDto;
import org.springframework.beans.factory.annotation.Autowired;

public class CommentKafkaTemplateConfig extends AbstractKafkaTemplateConfig<CommentEventDto> {
    public CommentKafkaTemplateConfig(
        @Autowired
        ObjectMapper objectMapper,
        Class<CommentEventDto> typeEvent
    ) {
        super(objectMapper, typeEvent);
    }
}
