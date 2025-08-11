package faang.school.postservice.kafka;

import faang.school.postservice.kafka.event.LikeEvent;
import faang.school.postservice.kafka.service.LikeProcessingService;
import faang.school.postservice.service.redis.entity.PostRedis;
import faang.school.postservice.service.redis.repository.PostRedisRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LikeProcessingServiceTest {

    @Mock
    PostRedisRepository postRedisRepository;

    @InjectMocks
    LikeProcessingService service;

    private static LikeEvent event(long postId) {
        LikeEvent e = new LikeEvent();
        e.setId(1L);
        e.setPostId(postId);
        e.setAuthorId(7L);
        e.setTimestamp(LocalDateTime.now());
        return e;
    }

    @Test
    void incrementsWhenPostExists() {
        PostRedis post = new PostRedis();
        post.setId(42L);
        post.setLikesCount(5L);

        when(postRedisRepository.findById(42L)).thenReturn(Optional.of(post));
        when(postRedisRepository.save(any(PostRedis.class))).thenAnswer(inv -> inv.getArgument(0));

        service.addLikeToCache(event(42L));

        assertEquals(6L, post.getLikesCount());
        verify(postRedisRepository).save(post);
    }

    @Test
    void skipsWhenPostNotFound() {
        when(postRedisRepository.findById(999L)).thenReturn(Optional.empty());

        service.addLikeToCache(event(999L));

        verify(postRedisRepository, never()).save(any());
    }
}
