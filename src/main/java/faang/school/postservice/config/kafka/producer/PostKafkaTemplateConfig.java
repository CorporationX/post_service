package faang.school.postservice.config.kafka.producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.kafka.event.PostEventDto;
import org.springframework.beans.factory.annotation.Autowired;

public class PostKafkaTemplateConfig extends AbstractKafkaTemplateConfig<PostEventDto> {
    public PostKafkaTemplateConfig(
        @Autowired
        ObjectMapper objectMapper,
        Class<PostEventDto> typeEvent
    ) {
        super(objectMapper, typeEvent);
    }
}
