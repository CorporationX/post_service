package faang.school.postservice.kafka;

import faang.school.postservice.kafka.event.CommentEvent;
import faang.school.postservice.kafka.service.CommentProcessingService;
import faang.school.postservice.mapper.CommentEventMapper;
import faang.school.postservice.service.redis.dto.CommentCacheDto;
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
import java.util.Comparator;
import java.util.Optional;
import java.util.TreeSet;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringJUnitConfig
@TestPropertySource(properties = {
        "app.redis.post.max-comments=3",
        "app.redis.post.retry=5",
        "app.redis.post.backoff-delay=10"
})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class CommentProcessingServiceRetryTest {

    @TestConfiguration
    @EnableRetry
    @Import(CommentProcessingService.class)
    static class Config {
        @Bean
        public PostRedisRepository postRedisRepository() {
            return mock(PostRedisRepository.class);
        }
        @Bean
        public CommentEventMapper commentEventMapper() {
            return mock(CommentEventMapper.class);
        }
    }

    @Autowired
    private CommentProcessingService processingService;

    @Autowired
    private PostRedisRepository postRedisRepository;

    @Autowired
    private CommentEventMapper commentEventMapper;

    private CommentEvent event;
    private PostRedis existing;
    private CommentCacheDto dto;

    @BeforeEach
    void setUp() {
        event = new CommentEvent();
        event.setId(10L);
        event.setPostId(42L);
        event.setAuthorId(7L);
        event.setContent("hello");
        event.setTimestamp(LocalDateTime.of(2025, 7, 27, 12, 0));

        existing = new PostRedis();
        existing.setId(42L);
        existing.setContent("post");
        existing.setAuthorId(1L);
        existing.setLikesCount(0L);
        existing.setLastComments(new TreeSet<>(Comparator.comparing(CommentCacheDto::getTimestamp)));

        dto = new CommentCacheDto();
        dto.setId(10L);
        dto.setAuthorId(7L);
        dto.setContent("hello");
        dto.setTimestamp(event.getTimestamp());
        dto.setLikesCount(0L);

        when(postRedisRepository.findById(42L)).thenReturn(Optional.of(existing));
        when(commentEventMapper.createDtoFromEvent(event)).thenReturn(dto);

        org.springframework.test.util.ReflectionTestUtils.setField(
                processingService, "maxCommentsInCache", 3);
    }

    @Test
    void whenOptimisticLockingOnFirstAttempt_shouldRetryAndThenSaveOnceMore() {

        when(postRedisRepository.findById(42L)).thenReturn(Optional.of(existing));
        when(postRedisRepository.save(any()))
                .thenThrow(new org.springframework.dao.OptimisticLockingFailureException("conflict"))
                .thenAnswer(inv -> inv.getArgument(0));

        processingService.addCommentToCache(event);

        verify(postRedisRepository, times(2)).save(any(PostRedis.class));
    }

    @Test
    void whenAllRetriesFail_shouldPropagateException() {
        when(postRedisRepository.findById(42L)).thenReturn(Optional.of(existing));
        when(postRedisRepository.save(any()))
                .thenThrow(new org.springframework.dao.OptimisticLockingFailureException("conflict"));

        assertThatThrownBy(() -> processingService.addCommentToCache(event))
                .isInstanceOf(RuntimeException.class)
                .hasCauseInstanceOf(org.springframework.dao.OptimisticLockingFailureException.class);

        verify(postRedisRepository, times(5)).save(any(PostRedis.class));
    }
}
