package faang.school.postservice.kafka.producer.post;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import school.faang.avro.post.PostCreateEvent;
import school.faang.avro.post.PostViewEvent;

@RequiredArgsConstructor
@Component
public class PostProducerImpl implements PostProducer {
    @Value("${spring.kafka.topics.post-view.name}")
    private String postViewTopic;

    @Value("${spring.kafka.topics.post.name}")
    private String postTopic;

    private final KafkaTemplate<String, PostViewEvent> onViewTemplate;
    private final KafkaTemplate<String, PostCreateEvent> onCreateTemplate;

    @Override
    public void onPostView(PostViewEvent event) {
        onViewTemplate.send(postViewTopic, String.valueOf(event.getPostId()), event);
    }

    @Override
    public void onPostCreate(PostCreateEvent event) {
        onCreateTemplate.send(postTopic, String.valueOf(event.getId()), event);
    }
}
