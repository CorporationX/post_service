package faang.school.postservice.kafka.producer;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.kafka.events.CommentEvent;
import faang.school.postservice.kafka.events.FeedDto;
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
    @Value("${spring.kafka.topic-name.heat}")
    private String heatTopic;

    private final KafkaTemplate<Long, Object> kafkaTemplate;
    //TODO need check if topics are correct
    public void sendPostFollowersEvent(PostFollowersEvent event) {
        kafkaTemplate.send(postTopic, event);
    }
    //TODO why do we need postId here
    public void sendPostViewEvent(Long postId, PostEvent event) {
        kafkaTemplate.send(postViewsTopic, event);
    }
    public void sendCommentEvent(Long postId, CommentEvent event){
        kafkaTemplate.send(commentTopic, event);
    }
    public void sendLikeEvent(Long postId, PostEvent event){
        kafkaTemplate.send(likeTopic, event);
    }

    public void sendFeedEvent(FeedDto event) {
        kafkaTemplate.send(heatTopic, event);
    }

    public void sendPostEvent(PostDto event) {
        kafkaTemplate.send(heatTopic, event);
    }
}