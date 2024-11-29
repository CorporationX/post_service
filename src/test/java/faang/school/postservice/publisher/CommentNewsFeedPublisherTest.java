package faang.school.postservice.publisher;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.mapper.comment.CommentMapperImpl;
import faang.school.postservice.protobuf.generate.FeedEventProto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommentNewsFeedPublisherTest {

    @Mock
    private KafkaTemplate<byte[], byte[]> kafkaTemplate;

    @Spy
    private CommentMapperImpl commentMapper;

    @InjectMocks
    private CommentNewsFeedPublisher commentNewsFeedPublisher;

    private String topicName;

    @BeforeEach
    void setUp() {
        topicName = "comment-news-feed";
        ReflectionTestUtils.setField(commentNewsFeedPublisher, "topicName", topicName);
    }

    @Test
    void publish_shouldSendCommentEventToKafka() {
        String content = "content";
        long postId = 123L;
        CommentDto commentDto = CommentDto.builder()
                .postId(postId)
                .content(content)
                .build();
        FeedEventProto.FeedEvent feedEvent = FeedEventProto.FeedEvent.newBuilder()
                .setPostId(postId)
                .setCommentContent(content)
                .build();
        byte[] byteEvent = feedEvent.toByteArray();

        commentNewsFeedPublisher.publish(commentDto);

        verify(commentMapper).toProto(commentDto);
        verify(kafkaTemplate).send(topicName, byteEvent);
    }
}
