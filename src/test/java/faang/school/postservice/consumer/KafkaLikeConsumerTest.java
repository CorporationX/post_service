package faang.school.postservice.consumer;

import faang.school.postservice.dto.event.LikeKafkaEvent;
import faang.school.postservice.model.redis.PostRedis;
import faang.school.postservice.repository.RedisPostRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.support.Acknowledgment;

import java.util.Optional;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class KafkaLikeConsumerTest {

    private static final Long AUTHOR_ID = 1L;
    private static final Long POST_ID = 1L;
    @Mock
    private RedisPostRepository redisPostRepository;

    @Mock
    private Acknowledgment acknowledgment;

    @InjectMocks
    private KafkaLikeConsumer kafkaLikeConsumer;

    @Test
    public void listenLikeEventWhenPostFoundInRedis() {
        LikeKafkaEvent likeKafkaEvent = new LikeKafkaEvent(AUTHOR_ID, POST_ID, null);
        PostRedis postRedis = PostRedis.builder().build();

        when(redisPostRepository.findById(POST_ID)).thenReturn(Optional.of(postRedis));

        kafkaLikeConsumer.listenLikeEvent(likeKafkaEvent, acknowledgment);

        verify(redisPostRepository).save(postRedis);
        verify(acknowledgment).acknowledge();
    }

    @Test
    public void listenLikeEventWhenPostNotFoundInRedis() {
        LikeKafkaEvent likeKafkaEvent = new LikeKafkaEvent(AUTHOR_ID, POST_ID, null);
        PostRedis postRedis = PostRedis.builder().build();

        when(redisPostRepository.findById(POST_ID)).thenReturn(Optional.empty());

        kafkaLikeConsumer.listenLikeEvent(likeKafkaEvent, acknowledgment);

        verify(redisPostRepository, never()).save(postRedis);
    }
}