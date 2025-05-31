package faang.school.postservice.service.comment;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.comment.CommentCreateDto;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.comment.CommentUpdateDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.mapper.comment.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.publisher.KafkaEventPublisher;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.service.post.PostService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import feign.FeignException;

import java.util.Collections;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private PostService postService;

    @Mock
    private UserServiceClient userServiceClient;

    @Mock
    private CommentMapper commentMapper;

    @Mock
    private KafkaEventPublisher kafkaEventPublisher;

    @InjectMocks
    private CommentService commentService;

    private CommentCreateDto commentCreateDto;
    private CommentUpdateDto commentUpdateDto;
    private Comment comment;
    private CommentDto commentDto;
    private UserDto userDto;
    private Post post;

    @BeforeEach
    void setUp() {
        commentCreateDto = CommentCreateDto.builder()
                .content("Test content")
                .authorId(1L)
                .postId(1L)
                .build();

        commentUpdateDto = CommentUpdateDto.builder()
                .id(1L)
                .content("Updated content")
                .authorId(1L)
                .build();

        post = Post.builder()
                .id(1L)
                .build();

        comment = Comment.builder()
                .id(1L)
                .content("Test content")
                .authorId(1L)
                .post(post)
                .createdAt(LocalDateTime.now())
                .build();

        commentDto = CommentDto.builder()
                .id(1L)
                .content("Test content")
                .authorId(1L)
                .postId(1L)
                .createdAt(LocalDateTime.now())
                .build();

        userDto = UserDto.builder()
                .id(1L)
                .username("testUser")
                .build();
    }

    @Test
    void createComment_WhenPostExists_ShouldCreateComment() {
        when(postService.existsById(1L)).thenReturn(true);
        when(userServiceClient.getUser(1L)).thenReturn(userDto);
        when(postService.getPostById(1L)).thenReturn(post);
        when(commentMapper.toEntity(commentCreateDto)).thenReturn(comment);
        when(commentRepository.save(comment)).thenReturn(comment);
        when(commentMapper.toDto(comment)).thenReturn(commentDto);

        CommentDto result = commentService.createComment(commentCreateDto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test content", result.getContent());
        verify(commentRepository, times(1)).save(comment);
    }

    @Test
    void createComment_WhenPostNotFound_ShouldThrowException() {
        when(postService.existsById(1L)).thenReturn(false);

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> commentService.createComment(commentCreateDto));
        assertEquals("Post with ID 1 does not exist.", exception.getMessage());
        verify(commentRepository, never()).save(any());
    }

    @Test
    void createComment_WhenUserNotFound_ShouldThrowException() {
        when(postService.existsById(1L)).thenReturn(true);
        when(userServiceClient.getUser(1L)).thenReturn(null);

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> commentService.createComment(commentCreateDto));
        assertEquals("User not found with ID: 1", exception.getMessage());
        verify(commentRepository, never()).save(any());
    }

    @Test
    void createComment_WhenFeignExceptionOccurs_ShouldHandle() {
        when(postService.existsById(1L)).thenReturn(true);
        when(userServiceClient.getUser(1L)).thenThrow(mock(FeignException.class));

        assertThrows(EntityNotFoundException.class,
                () -> commentService.createComment(commentCreateDto));
        verify(commentRepository, never()).save(any());
    }

    @Test
    void updateComment_WhenValidInput_ShouldUpdateComment() {
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));
        when(commentRepository.save(comment)).thenReturn(comment);
        when(commentMapper.toDto(comment)).thenReturn(commentDto);

        CommentDto result = commentService.updateComment(1L, commentUpdateDto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(commentMapper, times(1)).updateEntity(commentUpdateDto, comment);
        verify(commentRepository, times(1)).save(comment);
    }

    @Test
    void updateComment_WhenCommentNotFound_ShouldThrowException() {
        when(commentRepository.findById(1L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> commentService.updateComment(1L, commentUpdateDto));
        assertEquals("Comment not found with ID: 1", exception.getMessage());
        verify(commentRepository, never()).save(any());
    }

    @Test
    void updateComment_WhenAuthorMismatch_ShouldThrowException() {
        CommentUpdateDto wrongAuthorDto = CommentUpdateDto.builder()
                .id(1L)
                .content("Updated content")
                .authorId(2L)
                .build();

        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> commentService.updateComment(1L, wrongAuthorDto));
        assertEquals("Only the author of the comment can update it.", exception.getMessage());
        verify(commentRepository, never()).save(any());
    }

    @Test
    void getAllCommentsByPostId_WhenNoComments_ShouldReturnEmptyList() {
        when(commentRepository.findAllByPostId(1L)).thenReturn(Collections.emptyList());

        List<CommentDto> result = commentService.getAllCommentsByPostId(1L);

        assertTrue(result.isEmpty());
    }

    @Test
    void deleteComment_WhenValidInput_ShouldDeleteComment() {
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));

        commentService.deleteComment(1L, 1L);

        verify(commentRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteComment_WhenCommentNotFound_ShouldThrowException() {
        when(commentRepository.findById(1L)).thenReturn(Optional.empty());

        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> commentService.deleteComment(1L, 1L));
        assertEquals("Comment not found with ID: 1", exception.getMessage());
        verify(commentRepository, never()).deleteById(any());
    }

    @Test
    void deleteComment_WhenAuthorMismatch_ShouldThrowException() {
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));

        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> commentService.deleteComment(1L, 2L));
        assertEquals("Only the author of the comment can delete it.", exception.getMessage());
        verify(commentRepository, never()).deleteById(any());
    }
}