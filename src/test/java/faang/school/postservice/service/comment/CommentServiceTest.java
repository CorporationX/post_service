package faang.school.postservice.service.comment;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.mapper.comment.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.producer.KafkaCommentProducer;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.redis.RedisUserRepository;
import faang.school.postservice.validator.comment.CommentValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private CommentMapper commentMapper;

    @Mock
    private CommentValidator commentValidator;

    @Mock
    private KafkaCommentProducer commentProducer;

    @Mock
    private RedisUserRepository redisUserRepository;

    @Mock
    private UserServiceClient userServiceClient;

    @InjectMocks
    private CommentService commentService;

    private CommentDto commentDto;
    private Comment comment;
    private UserDto userDto;

    @BeforeEach
    public void setUp() {
        commentDto = CommentDto.builder()
                .id(1L)
                .content("Test content")
                .authorId(1L)
                .postId(1L)
                .build();

        comment = Comment.builder()
                .id(1L)
                .content("Test content")
                .authorId(1L)
                .build();

        userDto = UserDto.builder()
                .id(1L)
                .email("testEmail@mail.ru")
                .username("someName")
                .subscriberIds(new ArrayList<>())
                .build();

        lenient().when(commentMapper.toEntity(any(CommentDto.class))).thenReturn(comment);
        lenient().when(commentRepository.save(any(Comment.class))).thenReturn(comment);
        lenient().when(commentMapper.toDto(any(Comment.class))).thenReturn(commentDto);
        lenient().when(userServiceClient.getUser(anyLong())).thenReturn(userDto);
    }

    @Test
    @DisplayName("Создание комментария - Успешный сценарий")
    public void createComment_Success() {
        CommentDto result = commentService.createComment(1L, commentDto);

        assertNotNull(result);
        assertEquals("Test content", result.getContent());
        verify(commentValidator, times(1)).findPostById(1L);
        verify(commentRepository, times(1)).save(any(Comment.class));
    }

    @Test
    @DisplayName("Создание комментария - Пост не найден")
    public void createComment_PostNotFound() {
        doThrow(new RuntimeException("Post not found!")).when(commentValidator).findPostById(1L);

        Exception exception = assertThrows(RuntimeException.class, () -> {
            commentService.createComment(1L, commentDto);
        });

        assertEquals("Post not found!", exception.getMessage());
        verify(commentValidator, times(1)).findPostById(1L);
        verify(commentRepository, times(0)).save(any(Comment.class));
    }

    @Test
    @DisplayName("Обновление комментария - Успешный сценарий")
    public void updateComment_Success() {
        //when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));
        when(commentValidator.findCommentById(1L)).thenReturn(comment);

        CommentDto result = commentService.updateComment(1L, 1L, commentDto);

        assertNotNull(result);
        assertEquals("Test content", result.getContent());
        verify(commentValidator, times(1)).findPostById(1L);
        verify(commentValidator, times(1)).findCommentById(1L);
        verify(commentRepository, times(1)).save(any(Comment.class));
    }

    @Test
    @DisplayName("Обновление комментария - Комментарий не найден")
    public void updateComment_CommentNotFound() {
        doThrow(new RuntimeException("Comment not found!")).when(commentValidator).findCommentById(1L);

        Exception exception = assertThrows(RuntimeException.class, () -> {
            commentService.updateComment(1L, 1L, commentDto);
        });

        assertEquals("Comment not found!", exception.getMessage());
        verify(commentValidator, times(1)).findPostById(1L);
        verify(commentValidator, times(1)).findCommentById(1L);
        verify(commentRepository, times(0)).save(any(Comment.class));
    }

    @Test
    @DisplayName("Получение комментариев по посту - Успешный сценарий")
    public void getCommentsByPost_Success() {
        when(commentRepository.findAllByPostIdSorted(1L)).thenReturn(Collections.singletonList(comment));

        List<CommentDto> result = commentService.getCommentsByPost(1L);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals("Test content", result.get(0).getContent());
        verify(commentValidator, times(1)).findPostById(1L);
        verify(commentRepository, times(1)).findAllByPostIdSorted(1L);
    }

    @Test
    @DisplayName("Удаление комментария - Успешный сценарий")
    public void deleteComment_Success() {
        commentService.deleteComment(1L);

        verify(commentValidator, times(1)).findCommentById(1L);
        verify(commentRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Удаление комментария - Комментарий не найден")
    public void deleteComment_CommentNotFound() {
        doThrow(new RuntimeException("Comment not found!")).when(commentValidator).findCommentById(1L);

        Exception exception = assertThrows(RuntimeException.class, () -> {
            commentService.deleteComment(1L);
        });

        assertEquals("Comment not found!", exception.getMessage());
        verify(commentValidator, times(1)).findCommentById(1L);
        verify(commentRepository, times(0)).deleteById(1L);
    }
}
