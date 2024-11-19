package faang.school.postservice.config.kafka.producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.kafka.event.PostViewEventDto;
import org.springframework.beans.factory.annotation.Autowired;

public class PostViewKafkaTemplateConfig extends AbstractKafkaTemplateConfig<PostViewEventDto>{
    public PostViewKafkaTemplateConfig(
        @Autowired
        ObjectMapper objectMapper,
        Class<PostViewEventDto> typeEvent
    ) {
        super(objectMapper, typeEvent);
    }
}
