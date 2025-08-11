package faang.school.postservice.kafka;

import faang.school.postservice.kafka.event.LikeEvent;
import faang.school.postservice.kafka.service.LikeProcessingService;
import faang.school.postservice.service.redis.entity.PostRedis;
import faang.school.postservice.service.redis.repository.PostRedisRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringJUnitConfig
@TestPropertySource(properties = {
        "app.redis.post.retry=5",
        "app.redis.post.backoff-delay=0"
})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class LikeProcessingServiceRetryTest {
    @TestConfiguration
    @EnableRetry
    @Import(LikeProcessingService.class)
    static class Config {
        @Bean
        public PostRedisRepository postRedisRepository() {
            return mock(PostRedisRepository.class);
        }
    }

    @Autowired
    private LikeProcessingService likeProcessingService;

    @Autowired
    private PostRedisRepository postRedisRepository;

    private LikeEvent event;
    private PostRedis existing;

    @BeforeEach
    void setUp() {
        event = new LikeEvent();
        event.setId(10L);
        event.setPostId(42L);
        event.setAuthorId(7L);
        event.setTimestamp(LocalDateTime.of(2025, 7, 27, 12, 0));

        existing = new PostRedis();
        existing.setId(42L);
        existing.setAuthorId(1L);
        existing.setContent("post");
        existing.setLikesCount(0L);

        when(postRedisRepository.findById(42L)).thenReturn(Optional.of(existing));
    }

    @Test
    void incrementsWhenPostExists_saveOnce() {
        when(postRedisRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        likeProcessingService.addLikeToCache(event);

        assertEquals(1L, existing.getLikesCount());
        verify(postRedisRepository).save(existing);
    }

    @Test
    void whenOptimisticLockOnFirstAttempt_shouldRetryAndThenSaveOnceMore() {
        when(postRedisRepository.save(any()))
                .thenThrow(new org.springframework.dao.OptimisticLockingFailureException("conflict"))
                .thenAnswer(inv -> inv.getArgument(0));

        likeProcessingService.addLikeToCache(event);

        verify(postRedisRepository, times(2)).save(any(PostRedis.class));
    }

    @Test
    void whenAllRetriesFail_shouldPropagateException() {
        when(postRedisRepository.save(any()))
                .thenThrow(new org.springframework.dao.OptimisticLockingFailureException("conflict"));

        assertThatThrownBy(() -> likeProcessingService.addLikeToCache(event))
                .isInstanceOf(RuntimeException.class)
                .hasCauseInstanceOf(org.springframework.dao.OptimisticLockingFailureException.class);

        verify(postRedisRepository, times(5)).save(any(PostRedis.class));
    }

    @Test
    void whenPostNotFound_shouldSkipAndNotSave() {
        LikeEvent missing = new LikeEvent();
        missing.setId(11L);
        missing.setPostId(999L);
        missing.setAuthorId(7L);
        missing.setTimestamp(LocalDateTime.of(2025, 7, 27, 12, 1));

        when(postRedisRepository.findById(999L)).thenReturn(Optional.empty());

        likeProcessingService.addLikeToCache(missing);

        verify(postRedisRepository, never()).save(any());
    }
}
