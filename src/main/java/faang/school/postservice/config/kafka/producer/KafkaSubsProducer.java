package faang.school.postservice.config.kafka.producer;

import faang.school.postservice.dto.post.SubsDto;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaSubsProducer extends AbstractProducer<SubsDto> {
    private final NewTopic subsTheme;

    public KafkaSubsProducer(KafkaTemplate<String, Object> kafkaTemplate,
                             NewTopic subsTheme) {
        super(kafkaTemplate);
        this.subsTheme = subsTheme;
    }

    @Override
    public String getTheme() {
        return subsTheme.name();
    }
}
