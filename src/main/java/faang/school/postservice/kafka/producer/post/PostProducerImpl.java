package faang.school.postservice.kafka.producer.post;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import school.faang.avro.post.PostViewEvent;

@RequiredArgsConstructor
@Component
public class PostProducerImpl implements PostProducer {
    @Value("${spring.kafka.topics.post-view.name}")
    private String postViewTopic;

    private final KafkaTemplate<String, PostViewEvent> template;

    @Override
    public void onPostView(PostViewEvent event) {
        template.send(postViewTopic, String.valueOf(event.getPostId()), event);
    }
}
