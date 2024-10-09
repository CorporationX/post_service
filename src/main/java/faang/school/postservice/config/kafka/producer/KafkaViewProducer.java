package faang.school.postservice.config.kafka.producer;

import faang.school.postservice.dto.post.PostViewDto;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaViewProducer extends AbstractProducer<PostViewDto> {
    private final NewTopic viewTheme;

    public KafkaViewProducer(KafkaTemplate<String, Object> kafkaTemplate,
                             NewTopic viewTheme) {
        super(kafkaTemplate);
        this.viewTheme = viewTheme;
    }

    @Override
    public String getTheme() {
        return viewTheme.name();
    }
}
