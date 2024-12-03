package faang.school.postservice.consumer.post;

import faang.school.postservice.dto.cache.feed.FeedCacheDto;
import faang.school.postservice.event.kafka.post.PostCreatedEvent;
import faang.school.postservice.repository.cache.feed.FeedCacheRepositoryImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.support.Acknowledgment;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class KafkaPostConsumerTest {

    @InjectMocks
    private KafkaPostConsumer kafkaPostConsumer;

    @Mock
    private FeedCacheRepositoryImpl feedCacheRepository;

    @Mock
    private Acknowledgment acknowledgment;

    private static final Long ID_ONE = 1L;
    private static final Long ID_TWO = 2L;
    private static final Long ID_THREE = 3L;
    private boolean allProcessedSuccessfully;
    private PostCreatedEvent postCreatedEventtt;
    private FeedCacheDto feedCacheDto;
    private List<Long> subscribersIds;
    private TreeSet<Long> postsIds;
    private PostCreatedEvent event;

    @BeforeEach
    public void init() {
        allProcessedSuccessfully = true;
        subscribersIds = Arrays.asList(ID_TWO);
        postsIds = new TreeSet<>(Arrays.asList(ID_ONE, ID_TWO));
        feedCacheDto = FeedCacheDto.builder()
                .subscriberId(ID_TWO)
                .postsIds(postsIds)
                .build();
        postCreatedEventtt = PostCreatedEvent.builder()
                .postId(ID_THREE)
                .authorId(ID_TWO)
                .subscribers(subscribersIds)
                .build();
    }

    @Test
    @DisplayName("Success when listenPostEvent with existing FeedCacheDto")
    public void whenListenPostEventThenUpdateFeedCacheDto() {
        when(feedCacheRepository.findBySubscriberId(ID_TWO))
                .thenReturn(Optional.of(feedCacheDto));
        doNothing().when(feedCacheRepository).addPostId(feedCacheDto, ID_THREE);
        doNothing().when(feedCacheRepository).save(any(FeedCacheDto.class));

        kafkaPostConsumer.listenPostEvent(postCreatedEventtt, acknowledgment);

        assertEquals(ID_TWO, postCreatedEventtt.getSubscribers().get(0));
        verify(feedCacheRepository).findBySubscriberId(ID_TWO);
        verify(feedCacheRepository).addPostId(feedCacheDto, ID_THREE);
        verify(feedCacheRepository).save(any(FeedCacheDto.class));
    }

    @Test
    @DisplayName("Success when listenPostEvent with FeedCacheDto does not exist")
    public void whenListenPostEventWithDoesNotExistFeedCacheDtoThenUpdateFeedCacheDto() {
        event = PostCreatedEvent.builder()
                .postId(ID_THREE)
                .subscribers(Arrays.asList(ID_ONE))
                .build();
        when(feedCacheRepository.findBySubscriberId(ID_ONE))
                .thenReturn(Optional.empty());
        doNothing().when(feedCacheRepository).addPostId(any(FeedCacheDto.class), anyLong());
        doNothing().when(feedCacheRepository).save(any(FeedCacheDto.class));

        kafkaPostConsumer.listenPostEvent(event, acknowledgment);

        verify(feedCacheRepository).findBySubscriberId(ID_ONE);
        verify(feedCacheRepository).addPostId(any(FeedCacheDto.class), anyLong());
        verify(feedCacheRepository).save(any(FeedCacheDto.class));
    }
}