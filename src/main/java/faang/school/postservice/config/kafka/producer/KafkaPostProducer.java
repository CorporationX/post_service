package faang.school.postservice.config.kafka.producer;

import faang.school.postservice.dto.post.PostDto;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaPostProducer extends AbstractProducer<PostDto> {

    private final NewTopic postTheme;

    public KafkaPostProducer(KafkaTemplate<String, Object> kafkaTemplate,
                             NewTopic postTheme) {
        super(kafkaTemplate);
        this.postTheme = postTheme;
    }

    @Override
    public String getTheme() {
        return postTheme.name();
    }
}
