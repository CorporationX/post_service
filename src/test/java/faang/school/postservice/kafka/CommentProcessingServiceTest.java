package faang.school.postservice.kafka;

import faang.school.postservice.kafka.event.CommentEvent;
import faang.school.postservice.kafka.service.CommentProcessingService;
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
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Optional;
import java.util.TreeSet;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CommentProcessingServiceTest {

    @Mock
    private PostRedisRepository postRedisRepository;

    @Mock
    private CommentEventMapper commentEventMapper;

    @InjectMocks
    private CommentProcessingService processingService;

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
        event.setTimestamp(LocalDateTime.of(2025, 7, 27, 12, 0));

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
        existing.setLastComments(new TreeSet<>(Comparator.comparing(CommentCacheDto::getTimestamp)));

        ReflectionTestUtils.setField(processingService, "maxCommentsInCache", 3);

        when(commentEventMapper.createDtoFromEvent(event)).thenReturn(dto);
    }

    @Test
    void whenMoreThanMaxComments_shouldEvictOldest() {

        LocalDateTime now = event.getTimestamp();
        CommentCacheDto old0 = new CommentCacheDto();
        old0.setId(0L);
        old0.setAuthorId(1L);
        old0.setContent("old0");
        old0.setTimestamp(now.minusMinutes(10));
        existing.getLastComments().add(old0);

        CommentCacheDto old1 = new CommentCacheDto();
        old1.setId(1L);
        old1.setAuthorId(1L);
        old1.setContent("old1");
        old1.setTimestamp(now.minusMinutes(9));
        existing.getLastComments().add(old1);

        CommentCacheDto old2 = new CommentCacheDto();
        old2.setId(2L);
        old2.setAuthorId(1L);
        old2.setContent("old2");
        old2.setTimestamp(now.minusMinutes(8));
        existing.getLastComments().add(old2);

        when(postRedisRepository.findById(42L)).thenReturn(Optional.of(existing));

        processingService.addCommentToCache(event);

        verify(postRedisRepository).save(postCaptor.capture());
        TreeSet<CommentCacheDto> savedSet = postCaptor.getValue().getLastComments();


        assertThat(savedSet)
                .hasSize(3)
                .containsExactlyInAnyOrder(dto, old1, old2);

        assertThat(savedSet.stream()
                .map(CommentCacheDto::getId))
                .doesNotContain(0L);
    }
}
