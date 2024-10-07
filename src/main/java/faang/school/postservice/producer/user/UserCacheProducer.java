package faang.school.postservice.producer.user;

import faang.school.postservice.event.user.UserCacheEvent;
import faang.school.postservice.model.Post;
import faang.school.postservice.producer.AbstractProducer;
import faang.school.postservice.producer.PostServiceProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserCacheProducer extends AbstractProducer<UserCacheEvent> implements PostServiceProducer {
    public UserCacheProducer(KafkaTemplate<String, Object> kafkaTemplate,
                             @Value("${kafka.topic.user-cache-topic.name}") String topicName) {
        super(kafkaTemplate, topicName);
    }

    @Override
    public void send(Post post) {
        UserCacheEvent userCacheEvent = new UserCacheEvent(post.getAuthorId());
        sendEvent(userCacheEvent);
    }
}
