package faang.school.postservice.kafka.producer.like;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import school.faang.avro.post.LikePublishEvent;

@RequiredArgsConstructor
@Component
public class PostLikeProducerImpl implements PostLikeProducer {
    @Value("${spring.kafka.topics.post-like-publish.name}")
    private String publishLikeTopic;

    private final KafkaTemplate<String, LikePublishEvent> template;

    @Override
    public void onLikePublished(LikePublishEvent dto) {
        template.send(publishLikeTopic, String.valueOf(dto.getPostId()), dto);
    }
}
