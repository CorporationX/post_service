package faang.school.postservice.consumer;

import faang.school.postservice.dto.event.PostKafkaEvent;
import faang.school.postservice.model.redis.FeedRedis;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.RedisFeedRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.support.Acknowledgment;

import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class KafkaPostConsumerTest {

    private static final Long AUTHOR_ID = 1L;
    private static final Long SUBSCR_ID = 2L;

    @Mock
    private PostRepository postRepository;
    @Mock
    private RedisFeedRepository redisFeedRepository;
    @Mock
    private Acknowledgment acknowledgment;

    @InjectMocks
    private KafkaPostConsumer kafkaPostConsumer;


    @Test
    public void listenPostEventWhenFeedRedisNotNull() {
        PostKafkaEvent postKafkaEvent = new PostKafkaEvent(AUTHOR_ID, List.of(SUBSCR_ID));
        FeedRedis feedRedis = FeedRedis.builder()
                .id(SUBSCR_ID)
                .postIds(new TreeSet<>())
                .build();

        List<Long> authorPostIds = List.of(1L, 2L, 3L);

        when(redisFeedRepository.getById(SUBSCR_ID)).thenReturn(feedRedis);
        when(postRepository.findPostIdsByAuthorIdOrderByIdDesc(AUTHOR_ID)).thenReturn(authorPostIds);


        kafkaPostConsumer.listenPostEvent(postKafkaEvent, acknowledgment);

        verify(redisFeedRepository).save(feedRedis);
    }

    @Test
    public void listenPostEventWhenFeedRedisNull() {
        PostKafkaEvent postKafkaEvent = new PostKafkaEvent(AUTHOR_ID, List.of(SUBSCR_ID));
        List<Long> authorPostIds = List.of(1L, 2L, 3L);
        TreeSet<Long> sortIds = new TreeSet<>(Comparator.reverseOrder());
        FeedRedis feedRedis = FeedRedis.builder()
                .id(SUBSCR_ID)
                .postIds(sortIds)
                .build();

        when(redisFeedRepository.getById(SUBSCR_ID)).thenReturn(null);
        when(postRepository.findPostIdsByAuthorIdOrderByIdDesc(AUTHOR_ID)).thenReturn(authorPostIds);


        kafkaPostConsumer.listenPostEvent(postKafkaEvent, acknowledgment);

        verify(redisFeedRepository).save(feedRedis);
        verify(acknowledgment).acknowledge();
    }
}