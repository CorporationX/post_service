package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.exception.CommentNotFoundException;
import faang.school.postservice.exception.UserNotFoundException;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import feign.FeignException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private PostInternalService postInternalService;

    @Mock
    private UserServiceClient userServiceClient;

    @Mock
    private CommentMapper commentMapper;

    @Mock
    private CommentValidator commentValidator;

    @InjectMocks
    private CommentService commentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createComment_success() {
        Post post = new Post();
        post.setId(1L);

        final CommentDto dto = CommentDto.builder()
                .postId(post.getId())
                .authorId(100L)
                .content("Nice post")
                .build();

        Comment comment = new Comment();
        comment.setPost(post);
        comment.setAuthorId(100L);
        comment.setContent("Nice post");

        Comment saved = new Comment();
        saved.setId(10L);
        saved.setPost(post);
        saved.setAuthorId(100L);
        saved.setContent("Nice post");

        doCallRealMethod().when(commentValidator).validateCommentCreate(dto);
        when(postInternalService.findPostById(1L)).thenReturn(post);
        when(userServiceClient.getUser(100L)).thenReturn(null);
        when(commentMapper.toEntity(dto)).thenReturn(comment);
        when(commentRepository.save(comment)).thenReturn(saved);
        when(commentMapper.toDto(saved)).thenReturn(dto);

        CommentDto result = commentService.createComment(dto);

        assertEquals(dto.getContent(), result.getContent());
        assertEquals(dto.getPostId(), result.getPostId());
        assertEquals(dto.getAuthorId(), result.getAuthorId());

        verify(commentValidator).validateCommentCreate(dto);
        verify(postInternalService).findPostById(1L);
        verify(userServiceClient).getUser(100L);
        verify(commentRepository).save(comment);
    }

    @Test
    void createComment_userNotFound_throwsException() {
        final CommentDto dto = CommentDto.builder()
                .postId(1L)
                .authorId(999L)
                .content("Hello")
                .build();

        Post post = new Post();
        post.setId(1L);

        doCallRealMethod().when(commentValidator).validateCommentCreate(dto);
        when(postInternalService.findPostById(1L)).thenReturn(post);
        when(userServiceClient.getUser(999L)).thenThrow(FeignException.class);

        UserNotFoundException ex = assertThrows(UserNotFoundException.class,
                () -> commentService.createComment(dto));

        assertTrue(ex.getMessage().contains("User with id = 999 was not found"));

        verify(commentValidator).validateCommentCreate(dto);
        verify(postInternalService).findPostById(1L);
        verify(userServiceClient).getUser(999L);
        verify(commentRepository, never()).save(any());
    }

    @Test
    void getCommentsByPostId_returnsSortedComments() {
        Long postId = 1L;

        Post post = new Post();
        post.setId(postId);

        Comment comment1 = new Comment();
        comment1.setId(1L);
        comment1.setCreatedAt(LocalDateTime.now().minusDays(1));

        Comment comment2 = new Comment();
        comment2.setId(2L);
        comment2.setCreatedAt(LocalDateTime.now());

        CommentDto dto1 = CommentDto.builder().id(1L).build();
        CommentDto dto2 = CommentDto.builder().id(2L).build();

        when(postInternalService.findPostById(postId)).thenReturn(post);
        when(commentRepository.findAllByPostId(postId)).thenReturn(List.of(comment1, comment2));
        when(commentMapper.toDto(comment1)).thenReturn(dto1);
        when(commentMapper.toDto(comment2)).thenReturn(dto2);

        List<CommentDto> comments = commentService.getCommentsByPostId(postId);

        assertEquals(2, comments.size());
        assertEquals(dto2.getId(), comments.get(0).getId());
        assertEquals(dto1.getId(), comments.get(1).getId());

        verify(postInternalService).findPostById(postId);
        verify(commentRepository).findAllByPostId(postId);
    }

    @Test
    void updateComment_success() {
        Long commentId = 10L;
        Long userId = 100L;

        final CommentDto dto = CommentDto.builder()
                .content("Updated content")
                .build();

        Comment existing = new Comment();
        existing.setId(commentId);
        existing.setAuthorId(userId);
        existing.setContent("Old content");

        Comment saved = new Comment();
        saved.setId(commentId);
        saved.setAuthorId(userId);
        saved.setContent("Updated content");

        when(commentRepository.findById(commentId)).thenReturn(Optional.of(existing));
        doNothing().when(commentValidator).validateAuthor(existing, userId);
        doCallRealMethod().when(commentValidator).validateCommentUpdate(dto);
        when(commentRepository.save(existing)).thenReturn(saved);
        when(commentMapper.toDto(saved)).thenReturn(dto);

        CommentDto result = commentService.updateComment(commentId, dto, userId);

        assertEquals(dto.getContent(), result.getContent());

        verify(commentRepository).findById(commentId);
        verify(commentValidator).validateAuthor(existing, userId);
        verify(commentValidator).validateCommentUpdate(dto);
        verify(commentRepository).save(existing);
    }

    @Test
    void updateComment_commentNotFound_throwsException() {
        when(commentRepository.findById(999L)).thenReturn(Optional.empty());

        CommentNotFoundException ex = assertThrows(CommentNotFoundException.class,
                () -> commentService.updateComment(999L, new CommentDto(), 1L));

        assertTrue(ex.getMessage().contains("There is no comment with id: 999"));

        verify(commentRepository).findById(999L);
        verifyNoMoreInteractions(commentValidator);
        verify(commentRepository, never()).save(any());
    }

    @Test
    void deleteComment_success() {
        Long commentId = 10L;
        Long userId = 100L;

        Comment existing = new Comment();
        existing.setId(commentId);
        existing.setAuthorId(userId);

        CommentDto dto = CommentDto.builder()
                .id(commentId)
                .authorId(userId)
                .build();

        when(commentRepository.findById(commentId)).thenReturn(Optional.of(existing));
        doNothing().when(commentValidator).validateAuthor(existing, userId);
        doNothing().when(commentRepository).delete(existing);
        when(commentMapper.toDto(existing)).thenReturn(dto);

        CommentDto result = commentService.deleteComment(commentId, userId);

        assertEquals(dto.getId(), result.getId());
        assertEquals(dto.getAuthorId(), result.getAuthorId());

        verify(commentRepository).findById(commentId);
        verify(commentValidator).validateAuthor(existing, userId);
        verify(commentRepository).delete(existing);
        verify(commentMapper).toDto(existing);
    }

    @Test
    void deleteComment_commentNotFound_throwsException() {
        when(commentRepository.findById(1000L)).thenReturn(Optional.empty());

        CommentNotFoundException ex = assertThrows(CommentNotFoundException.class,
                () -> commentService.deleteComment(1000L, 1L));

        assertTrue(ex.getMessage().contains("There is no comment with id: 1000"));

        verify(commentRepository).findById(1000L);
        verify(commentValidator, never()).validateAuthor(any(), any());
        verify(commentRepository, never()).delete(any());
    }

    @Test
    void getCommentById_success() {
        Long commentId = 10L;
        Comment comment = new Comment();
        comment.setId(commentId);

        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

        Comment result = commentService.getCommentById(commentId);

        assertEquals(comment, result);
        verify(commentRepository).findById(commentId);
    }

    @Test
    void getCommentById_notFound_throwsException() {
        when(commentRepository.findById(1000L)).thenReturn(Optional.empty());

        CommentNotFoundException ex = assertThrows(CommentNotFoundException.class,
                () -> commentService.getCommentById(1000L));

        assertTrue(ex.getMessage().contains("There is no comment with id: 1000"));
        verify(commentRepository).findById(1000L);
    }
}
