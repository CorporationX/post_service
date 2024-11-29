package faang.school.postservice.listener;

import com.google.protobuf.InvalidProtocolBufferException;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.mapper.comment.CommentMapper;
import faang.school.postservice.protobuf.generate.FeedEventProto;
import faang.school.postservice.service.cache.SingleCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CommentNewsFeedListener implements KafkaEventListener<byte[]> {

    private final SingleCacheService<Long, CommentDto> cacheService;
    private final CommentMapper commentMapper;

    @Override
    @KafkaListener(topics = "${spring.kafka.topics.comment-for-feed.name}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "messageListenerContainer")
    public void onMessage(byte[] byteFeedEvent, Acknowledgment acknowledgment) {
        try {
            FeedEventProto.FeedEvent feedEvent = FeedEventProto.FeedEvent.parseFrom(byteFeedEvent);
            CommentDto comment = commentMapper.toDto(feedEvent);
            cacheService.save(feedEvent.getPostId(), comment);
            acknowledgment.acknowledge();

        } catch (InvalidProtocolBufferException exception) {
            log.error("Error while parsing feedEvent", exception);
            throw new RuntimeException(exception);
        }
    }
}
