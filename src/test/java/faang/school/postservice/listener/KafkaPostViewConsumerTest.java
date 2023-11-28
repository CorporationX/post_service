package faang.school.postservice.listener;

import faang.school.postservice.dto.kafka.PostViewEvent;
import faang.school.postservice.service.FeedService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class KafkaPostViewConsumerTest {

    @Mock
    private FeedService feedService;
    @InjectMocks
    private KafkaPostViewConsumer kafkaPostViewConsumer;

    @Test
    void listenPostViewEventTest() {
        PostViewEvent event = PostViewEvent.builder().postId(1L).build();
        kafkaPostViewConsumer.listenPostViewEvent(event);
        verify(feedService).incrementPostView(1L);
    }
}