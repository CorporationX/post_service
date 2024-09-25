package faang.school.postservice.service.publisher;

import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.event.comment.CommentEvent;
import faang.school.postservice.event.like.LikeEvent;
import faang.school.postservice.mapper.comment.CommentMapper;
import faang.school.postservice.mapper.like.LikeEventMapper;
import faang.school.postservice.messaging.publisher.comment.CommentEventPublisher;
import faang.school.postservice.messaging.publisher.comment.KafkaCommentProducer;
import faang.school.postservice.messaging.publisher.like.KafkaLikeProducer;
import faang.school.postservice.messaging.publisher.like.LikeEventPublisher;
import faang.school.postservice.model.Comment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EventPublisherService {
    private final LikeEventMapper likeEventMapper;
    private final CommentMapper commentMapper;
    private final LikeEventPublisher likeEventPublisher;
    private final CommentEventPublisher commentEventPublisher;
    private final KafkaCommentProducer kafkaCommentProducer;
    private final KafkaLikeProducer kafkaLikeProducer;

    public void publishLikeEvent(LikeDto likeDto) {
        LikeEvent likeEvent = likeEventMapper.toLikeEvent(likeDto);
        likeEventPublisher.publish(likeEvent);
        kafkaLikeProducer.publish(likeEvent);
    }

    public void publishCommentEvent(Comment comment) {
        CommentEvent commentEvent = commentMapper.toEvent(comment);
        commentEventPublisher.publish(commentEvent);
        kafkaCommentProducer.publish(commentEvent);
    }
}
