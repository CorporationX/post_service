package faang.school.postservice.service.feed.heat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.comment.CommentFeedDto;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.service.cache.RedisCacheService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.util.ReflectionTestUtils;

import java.sql.Timestamp;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AddInfoAboutCommentTest {

    @InjectMocks
    private AddInfoAboutComment addInfoAboutComment;

    @Mock
    private CommentMapper commentMapper;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private RedisCacheService redisCacheService;

    @Mock
    private ObjectMapper objectMapper;

    @Value("${spring.data.redis.settings.maxSizeComment}")
    private int maxSizeComment = 5;

    @BeforeEach
    public void setUp() {
        ReflectionTestUtils.setField(addInfoAboutComment, "maxSizeComment", maxSizeComment);
    }

    @Test
    public void testAddInfoToRedis() throws JsonProcessingException {
        Long userId = 1L;
        Long postId = 1L;

        Comment comment = Comment.builder().id(1L).build();
        CommentFeedDto commentFeedDto = CommentFeedDto.builder().id(1L).build();
        List<Comment> commentList = List.of(comment);

        Timestamp updatedTime = new Timestamp(System.currentTimeMillis());
        String commentJson = "{\"id\":1}";

        when(commentRepository.findLastLimitComment(postId, maxSizeComment)).thenReturn(commentList);
        when(commentMapper.toFeedDto(any(Comment.class))).thenReturn(commentFeedDto);
        when(commentRepository.getUpdatedTime(commentFeedDto.getId())).thenReturn(updatedTime);
        when(objectMapper.writeValueAsString(commentFeedDto)).thenReturn(commentJson);

        addInfoAboutComment.addInfoToRedis(userId, postId);

        verify(commentRepository, times(1)).findLastLimitComment(postId, maxSizeComment);
        verify(commentMapper, times(1)).toFeedDto(any(Comment.class));
        verify(commentRepository, times(1)).getUpdatedTime(commentFeedDto.getId());
        verify(objectMapper, times(1)).writeValueAsString(commentFeedDto);
        verify(redisCacheService, times(1)).addCommentToCache(postId, commentJson, updatedTime.getTime());
    }

    @Test
    public void testAddInfoToRedisWithJsonProcessingException() throws JsonProcessingException {
        Long userId = 1L;
        Long postId = 1L;

        Comment comment = Comment.builder().id(1L).build();
        CommentFeedDto commentFeedDto = CommentFeedDto.builder().id(1L).build();
        List<Comment> commentList = List.of(comment);

        Timestamp updatedTime = new Timestamp(System.currentTimeMillis());

        when(commentRepository.findLastLimitComment(postId, maxSizeComment)).thenReturn(commentList);
        when(commentMapper.toFeedDto(any(Comment.class))).thenReturn(commentFeedDto);
        when(commentRepository.getUpdatedTime(commentFeedDto.getId())).thenReturn(updatedTime);
        when(objectMapper.writeValueAsString(commentFeedDto)).thenThrow(new JsonProcessingException("Error") {});

        RuntimeException exception = assertThrows(RuntimeException.class, () -> addInfoAboutComment.addInfoToRedis(userId, postId));

        assertEquals("Error", exception.getCause().getMessage());

        verify(commentRepository, times(1)).findLastLimitComment(postId, maxSizeComment);
        verify(commentMapper, times(1)).toFeedDto(any(Comment.class));
        verify(commentRepository, times(1)).getUpdatedTime(commentFeedDto.getId());
        verify(objectMapper, times(1)).writeValueAsString(commentFeedDto);
        verify(redisCacheService, times(0)).addCommentToCache(any(Long.class), any(String.class), any(Long.class));
    }
}
