package faang.school.postservice.kafka;

import faang.school.postservice.kafka.consumer.KafkaCommentConsumer;
import faang.school.postservice.kafka.event.CommentEvent;
import faang.school.postservice.mapper.CommentEventMapper;
import faang.school.postservice.service.redis.dto.CommentCacheDto;
import faang.school.postservice.service.redis.entity.PostRedis;
import faang.school.postservice.service.redis.repository.PostRedisRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.TreeSet;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class KafkaCommentConsumerTest {

    @Mock
    private PostRedisRepository postRedisRepository;

    @Mock
    private CommentEventMapper mapper;

    @Mock
    private Acknowledgment ack;

    @InjectMocks
    private KafkaCommentConsumer consumer;

    @Captor
    private ArgumentCaptor<PostRedis> postCaptor;

    private CommentEvent event;
    private CommentCacheDto dto;
    private PostRedis existing;

    @BeforeEach
    void setUp() {
        event = new CommentEvent();
        event.setId(10L);
        event.setPostId(42L);
        event.setAuthorId(7L);
        event.setContent("hello");
        event.setTimestamp(LocalDateTime.of(2025,7,27,12,0));

        dto = new CommentCacheDto();
        dto.setId(10L);
        dto.setAuthorId(7L);
        dto.setContent("hello");
        dto.setTimestamp(event.getTimestamp());
        dto.setLikesCount(0L);

        existing = new PostRedis();
        existing.setId(42L);
        existing.setContent("post");
        existing.setAuthorId(1L);
        existing.setLikesCount(0L);
        existing.setLastComments(new TreeSet<>());

        ReflectionTestUtils.setField(consumer, "maxCommentsInCache", 3);
        ReflectionTestUtils.setField(consumer, "retries", 2);

        when(mapper.createDtoFromEvent(event)).thenReturn(dto);
    }

    @Test
    void whenPostNotInCache_shouldAckAndSkip() {
        when(postRedisRepository.findById(42L)).thenReturn(Optional.empty());

        consumer.onCommentEvent(event, ack);

        verify(postRedisRepository, never()).save(any());
        verify(ack).acknowledge();
    }

    @Test
    void whenFirstComment_shouldAddAndAck() {
        when(postRedisRepository.findById(42L)).thenReturn(Optional.of(existing));

        consumer.onCommentEvent(event, ack);

        verify(postRedisRepository).save(postCaptor.capture());
        PostRedis saved = postCaptor.getValue();

        assertThat(saved.getLastComments()).contains(dto);
        verify(ack).acknowledge();
    }

    @Test
    void whenMoreThanMaxComments_shouldEvictOldest() {
        LocalDateTime now = event.getTimestamp();
        for (int i = 0; i < 3; i++) {
            CommentCacheDto old = new CommentCacheDto();
            old.setId((long)i);
            old.setAuthorId(1L);
            old.setContent("x");
            old.setTimestamp(now.minusMinutes(10 - i));
            existing.getLastComments().add(old);
        }
        when(postRedisRepository.findById(42L)).thenReturn(Optional.of(existing));

        consumer.onCommentEvent(event, ack);

        verify(postRedisRepository).save(postCaptor.capture());
        TreeSet<CommentCacheDto> set = postCaptor.getValue().getLastComments();
        assertThat(set).hasSize(3).contains(dto);
        verify(ack).acknowledge();
    }

    @Test
    void whenOptimisticLockingOnFirstAttempt_shouldRetryAndThenAck() {
        when(postRedisRepository.findById(42L)).thenReturn(Optional.of(existing));

        when(postRedisRepository.save(any()))
                .thenThrow(new OptimisticLockingFailureException("conflict"))
                .thenAnswer(invocation -> invocation.getArgument(0));

        consumer.onCommentEvent(event, ack);

        verify(postRedisRepository, times(2)).save(any());
        verify(ack).acknowledge();
    }

    @Test
    void whenAlwaysOptimisticLockingAndExhaustRetries_shouldThrow() {
        when(postRedisRepository.findById(42L)).thenReturn(Optional.of(existing));
        doThrow(new OptimisticLockingFailureException("X"))
                .when(postRedisRepository).save(any());

        assertThatThrownBy(() -> consumer.onCommentEvent(event, ack))
                .isInstanceOf(OptimisticLockingFailureException.class);

        verify(postRedisRepository, times(2)).save(any());
        verify(ack, never()).acknowledge();
    }
}
