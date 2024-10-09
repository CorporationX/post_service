package faang.school.postservice.config.kafka.producer;

import faang.school.postservice.dto.like.LikeDto;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaLikeProducer extends AbstractProducer<LikeDto> {
    private final NewTopic likeTheme;

    public KafkaLikeProducer(KafkaTemplate<String, Object> kafkaTemplate,
                             NewTopic likeTheme) {
        super(kafkaTemplate);
        this.likeTheme = likeTheme;
    }

    @Override
    public String getTheme() {
        return likeTheme.name();
    }
}
