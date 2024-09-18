package faang.school.postservice.service.publisher;

import faang.school.postservice.dto.event.CommentEvent;
import faang.school.postservice.event.comment.CommentsEvent;
import faang.school.postservice.event.like.LikeEvent;
import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.event.post.FollowersPostEvent;
import faang.school.postservice.event.post.PostEvent;
import faang.school.postservice.mapper.comment.CommentMapper;
import faang.school.postservice.mapper.comment.PostViewMapper;
import faang.school.postservice.mapper.like.LikeEventMapper;
import faang.school.postservice.mapper.post.PostMapper;
import faang.school.postservice.messaging.kafka.pulisher.comment.KafkaCommentPublisher;
import faang.school.postservice.messaging.kafka.pulisher.like.KafkaLikesPublisher;
import faang.school.postservice.messaging.kafka.pulisher.post.KafkaFollowersPostPublisher;
import faang.school.postservice.messaging.kafka.pulisher.post.KafkaPostViewPublisher;
import faang.school.postservice.messaging.redis.publisher.comment.CommentEventPublisher;
import faang.school.postservice.messaging.redis.publisher.like.LikeEventPublisher;
import faang.school.postservice.messaging.redis.publisher.post.PostEventPublisher;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventPublisherService {
    /**
     * Publishing to Kafka
     */
    private final KafkaFollowersPostPublisher kafkaFollowersPostPublisher;
    private final KafkaCommentPublisher kafkaCommentPublisher;
    private final KafkaPostViewPublisher kafkaPostViewPublisher;
    private final KafkaLikesPublisher kafkaLikesPublisher;

    /**
     * Publishing to Redis
     */
    private final LikeEventPublisher likeEventPublisher;
    private final CommentEventPublisher commentEventPublisher;
    private final PostEventPublisher postEventPublisher;

    /**
     * Mapper to Event
     */
    private final LikeEventMapper likeEventMapper;
    private final PostViewMapper postViewMapper;
    private final PostMapper postMapper;
    private final CommentMapper commentMapper;

    public void sendFollowersEventToKafka(long authorId, List<Long> followersIds){
        FollowersPostEvent followersPostEvent = FollowersPostEvent.builder()
                .authorId(authorId)
                .followersIds(followersIds)
                .build();
        kafkaFollowersPostPublisher.publish(followersPostEvent);
    }

    public void sendCommentEventToKafka(Comment comment){
        CommentsEvent commentsEvent = commentMapper.toCommentsEvent(comment);
        kafkaCommentPublisher.publish(commentsEvent);
    }

    public void sendPostViewEventToKafka(Post post){
        PostEvent postEvent = postViewMapper.toPostEvent(post);
        kafkaPostViewPublisher.publish(postEvent);
    }

    public void sendLikesEventToKafka(LikeDto likeDto){
        LikeEvent likeEvent = likeEventMapper.toLikeEvent(likeDto);
        kafkaLikesPublisher.publish(likeEvent);
    }

    public void submitEvent(LikeDto likeDto) {
        LikeEvent likeEvent = likeEventMapper.toLikeEvent(likeDto);
        likeEventPublisher.publish(likeEvent);
    }

    public void sendCommentEventToRedis(Comment comment) {
        CommentEvent commentsEvent = commentMapper.toEvent(comment);
        commentEventPublisher.publish(commentsEvent);
    }

    public void sendPostEventToRedis(Post post) {
        PostEvent postEvent = postMapper.toPostEvent(post);
        postEventPublisher.publish(postEvent);
    }
}