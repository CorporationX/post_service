package faang.school.postservice.consumer;

import com.google.protobuf.InvalidProtocolBufferException;
import faang.school.postservice.dto.comment.CommentPublishedEvent;
import faang.school.postservice.mapper.comment.CommentPublishedEventMapper;
import faang.school.postservice.protobuf.generate.CommentPublishedEventProto;
import faang.school.postservice.service.FeedService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaCommentConsumer implements KafkaConsumer<byte[]> {
    private final CommentPublishedEventMapper mapper;
    private final FeedService feedService;

    @Override
    @KafkaListener(topics = {"${spring.kafka.topics.comments.name}"}, groupId = "first")
    public void processEvent(byte[] message) throws InvalidProtocolBufferException {
        CommentPublishedEvent event = mapper.toEvent(
                CommentPublishedEventProto.CommentPublishedEvent.parseFrom(message)
        );
        feedService.addNewComment(event);
    }
}
