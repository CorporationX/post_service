package faang.school.postservice.listener;

import faang.school.postservice.dto.PostPair;
import faang.school.postservice.dto.kafka.EventAction;
import faang.school.postservice.dto.kafka.PostEvent;
import faang.school.postservice.service.FeedService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class KafkaPostConsumerTest {

    @Mock
    private FeedService feedService;
    @InjectMocks
    private KafkaPostConsumer kafkaPostConsumer;

    private final Long postId = 1L;
    private final LocalDateTime publishedAt = LocalDateTime.now().minusMonths(1);

    @Test
    void listenPostEventCreateActionScenarioTest() {
        PostPair postPair = PostPair.builder()
                .postId(postId)
                .publishedAt(publishedAt)
                .build();
        List<Long> followeeIds = List.of(1L, 2L);

        PostEvent event = PostEvent.builder()
                .postPair(postPair)
                .followersIds(followeeIds)
                .eventAction(EventAction.CREATE)
                .build();

        kafkaPostConsumer.listenPostEvent(event);

        verify(feedService, times(2)).saveSinglePostToFeed(anyLong(), any(PostPair.class));
    }

    @Test
    void listenPostEventUpdateActionScenarioTest() {
        PostPair postPair = PostPair.builder()
                .postId(postId)
                .publishedAt(publishedAt)
                .build();

        List<Long> followeeIds = List.of(1L, 2L);

        PostEvent event = PostEvent.builder()
                .postPair(postPair)
                .followersIds(followeeIds)
                .eventAction(EventAction.UPDATE)
                .build();

        kafkaPostConsumer.listenPostEvent(event);

        verify(feedService).updateSinglePostInRedis(postId);
    }

    @Test
    void listenPostEventDeleteActionScenarioTest() {
        PostPair postPair = PostPair.builder()
                .postId(postId)
                .publishedAt(publishedAt)
                .build();

        List<Long> followeeIds = List.of(1L, 2L);

        PostEvent event = PostEvent.builder()
                .postPair(postPair)
                .followersIds(followeeIds)
                .eventAction(EventAction.DELETE)
                .build();

        kafkaPostConsumer.listenPostEvent(event);

        verify(feedService).deleteSinglePostInFeed(followeeIds, postId);
    }
}