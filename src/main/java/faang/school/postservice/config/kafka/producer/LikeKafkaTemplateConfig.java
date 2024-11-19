package faang.school.postservice.config.kafka.producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.kafka.event.LikeEventDto;
import org.springframework.beans.factory.annotation.Autowired;

public class LikeKafkaTemplateConfig extends AbstractKafkaTemplateConfig<LikeEventDto> {
    public LikeKafkaTemplateConfig(
        @Autowired
        ObjectMapper objectMapper,
        Class<LikeEventDto> typeEvent
    ) {
        super(objectMapper, typeEvent);
    }
}
