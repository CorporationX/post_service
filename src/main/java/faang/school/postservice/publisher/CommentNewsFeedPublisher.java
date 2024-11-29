package faang.school.postservice.publisher;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.mapper.comment.CommentMapper;
import faang.school.postservice.protobuf.generate.FeedEventProto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommentNewsFeedPublisher implements EventPublisher<CommentDto> {

    private final KafkaTemplate<byte[], byte[]> kafkaTemplate;
    private final CommentMapper commentMapper;

    @Value("${spring.kafka.topics.comment-for-feed.name}")
    private String topicName;

    @Override
    public void publish(CommentDto commentDto) {
        FeedEventProto.FeedEvent event = commentMapper.toProto(commentDto);
        byte[] byteEvent = event.toByteArray();
        kafkaTemplate.send(topicName, byteEvent);
    }
}