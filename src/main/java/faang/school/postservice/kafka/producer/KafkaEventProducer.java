package faang.school.postservice.kafka.producer;

import faang.school.postservice.kafka.events.CommentEvent;
import faang.school.postservice.kafka.events.PostEvent;
import faang.school.postservice.kafka.events.PostFollowersEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaEventProducer {
    @Value("${spring.kafka.topic-name.posts:posts}")
    private String postTopic;
    @Value("${spring.kafka.topic-name.post-views-topic}")
    private String postViewsTopic;
    @Value("${spring.kafka.topic-name.comments:comments}")
    private String commentTopic;
    @Value("${spring.kafka.topic-name.posts:likes}")
    private String likeTopic;

    private final KafkaTemplate<Long, Object> kafkaTemplate;
    //TODO need check if topics are correct
    public void sendPostFollowersEvent(Long postId, PostFollowersEvent event) {
        kafkaTemplate.send(postTopic, postId, event);
    }
    public void sendPostViewEvent(Long postId, PostEvent event) {
        kafkaTemplate.send(postViewsTopic, postId, event);
    }
    public void sendCommentEvent(Long postId, CommentEvent event){
        kafkaTemplate.send(commentTopic, postId, event);
    }
    public void sendLikeEvent(Long postId, PostEvent event){
        kafkaTemplate.send(commentTopic, postId, event);
    }
}