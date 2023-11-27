package faang.school.postservice.listener;

import faang.school.postservice.dto.PostPair;
import faang.school.postservice.dto.kafka.HeatFeedEvent;
import faang.school.postservice.service.FeedService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class KafkaHeatPostConsumerTest {

    @Mock
    private FeedService feedService;
    @InjectMocks
    private KafkaHeatPostConsumer kafkaHeatPostConsumer;

    private final Long userId = 1L;
    private final Long postId = 1L;

    private final LocalDateTime publishedAt = LocalDateTime.now().minusMonths(1);

    @Test
    void listenFeedHeatEventTest() {
        PostPair postPair = PostPair.builder()
                .postId(postId)
                .publishedAt(publishedAt)
                .build();
        HeatFeedEvent event = HeatFeedEvent.builder()
                .userId(userId)
                .postPair(postPair)
                .build();

        kafkaHeatPostConsumer.listenFeedHeatEvent(event);

        verify(feedService).saveSinglePostToFeed(userId, postPair);
    }
}