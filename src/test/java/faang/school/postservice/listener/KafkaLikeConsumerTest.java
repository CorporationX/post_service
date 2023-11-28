package faang.school.postservice.listener;

import faang.school.postservice.dto.kafka.LikeAction;
import faang.school.postservice.dto.kafka.LikeEvent;
import faang.school.postservice.service.FeedService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class KafkaLikeConsumerTest {

    @Mock
    private FeedService feedService;
    @InjectMocks
    private KafkaLikeConsumer kafkaLikeConsumer;

    private final Long postId = 1L;
    private final Long commentId = 1L;
    private final Long authorId = 1L;

    @Test
    void listenLikeEventLikePostScenarioTest() {
        LikeEvent event = LikeEvent.builder()
                .postId(postId)
                .commentId(null)
                .authorId(authorId)
                .likeAction(LikeAction.ADD)
                .build();

        kafkaLikeConsumer.listenLikeEvent(event);

        verify(feedService).incrementOrDecrementPostLike(postId, LikeAction.ADD);
    }

    @Test
    void listenLikeEventUnlikePostScenarioTest() {
        LikeEvent event = LikeEvent.builder()
                .postId(postId)
                .commentId(null)
                .authorId(authorId)
                .likeAction(LikeAction.REMOVE)
                .build();

        kafkaLikeConsumer.listenLikeEvent(event);

        verify(feedService).incrementOrDecrementPostLike(postId, LikeAction.REMOVE);
    }

    @Test
    void listenLikeEventLikeCommentScenarioTest() {
        LikeEvent event = LikeEvent.builder()
                .postId(postId)
                .commentId(commentId)
                .authorId(authorId)
                .likeAction(LikeAction.ADD)
                .build();

        kafkaLikeConsumer.listenLikeEvent(event);

        verify(feedService).incrementOrDecrementPostCommentLike(postId, commentId, LikeAction.ADD);
    }

    @Test
    void listenLikeEventUnlikeCommentScenarioTest() {
        LikeEvent event = LikeEvent.builder()
                .postId(postId)
                .commentId(commentId)
                .authorId(authorId)
                .likeAction(LikeAction.REMOVE)
                .build();

        kafkaLikeConsumer.listenLikeEvent(event);

        verify(feedService).incrementOrDecrementPostCommentLike(postId, commentId, LikeAction.REMOVE);
    }
}