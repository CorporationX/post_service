package faang.school.postservice.service;

import faang.school.postservice.dto.redis.CommentRedisDto;
import faang.school.postservice.dto.redis.PostRedisDto;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.messaging.kafka.events.CommentEvent;
import faang.school.postservice.repository.redis.RedisPostRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.LinkedHashSet;
import java.util.Optional;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RedisCommentServiceTest {
    @InjectMocks
    private RedisCommentService redisCommentService;
    @Mock
    private RedisTemplate<Long, Object> redisTemplate;
    @Mock
    private RedisPostRepository redisPostRepository;
    @Mock
    private CommentMapper commentMapper;

    @Test
    public void testAddCommentToPost() {
        CommentEvent commentEvent = CommentEvent.builder()
                .id(1L)
                .content("test")
                .authorId(2L)
                .postId(3L)
                .build();
        CommentRedisDto comment = CommentRedisDto.builder()
                .id(1L)
                .content("test")
                .authorId(2L)
                .postId(3L)
                .build();
        PostRedisDto post = PostRedisDto.builder()
                .comments(new LinkedHashSet<CommentRedisDto>() {{
                    add(comment);
                }})
                .build();

        when(redisPostRepository.findById(commentEvent.getPostId())).thenReturn(Optional.of(post));
        when(commentMapper.toRedisDto(commentEvent)).thenReturn(comment);

        redisCommentService.addCommentToPost(commentEvent);

        verify(redisPostRepository, times(1)).save(post);
    }
}
