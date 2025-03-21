package faang.school.postservice.util.comment;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.exception.CommentValidationException;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.service.CommentServiceImpl;
import faang.school.postservice.service.PostService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CommentServiceImplTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private PostService postService;

    @Mock
    private UserServiceClient userServiceClient;

    @Mock
    private CommentMapper commentMapper;

    @InjectMocks
    private CommentServiceImpl commentService;

    private CommentDto commentDto;
    private Comment comment;
    private Post post;

    @BeforeEach
    void setUp() {
        commentDto = CommentDto.builder()
                .id(1L)
                .content("Test comment")
                .authorId(1L)
                .postId(1L)
                .build();

        comment = Comment.builder()
                .id(1L)
                .content("Test comment")
                .authorId(1L)
                .build();

        post = Post.builder()
                .id(1L)
                .build();
    }

    @Test
    void testCreateComment_Success() {
        when(postService.findById(1L)).thenReturn(post);
        when(commentMapper.toEntity(commentDto)).thenReturn(comment);
        when(commentRepository.save(comment)).thenReturn(comment);
        when(commentMapper.toDto(comment)).thenReturn(commentDto);

        CommentDto result = commentService.createComment(1L, commentDto);

        assertNotNull(result);
        assertEquals(commentDto, result);
        verify(commentRepository, times(1)).save(comment);
        verify(commentMapper, times(1)).toEntity(commentDto);
        verify(commentMapper, times(1)).toDto(comment);
    }

    @Test
    void testCreateComment_ThrowsExceptionWhenCommentDtoIsNull() {
        assertThrows(CommentValidationException.class, () -> commentService.createComment(1L, null));
    }

    @Test
    void testCreateComment_ThrowsExceptionWhenPostIdIsNull() {
        assertThrows(CommentValidationException.class, () -> commentService.createComment(null, commentDto));
    }

    @Test
    void testCreateComment_ThrowsExceptionWhenAuthorIdIsNull() {
        CommentDto invalidCommentDto = CommentDto.builder()
                .content("Test comment")
                .postId(1L)
                .build();

        assertThrows(CommentValidationException.class, () -> commentService.createComment(1L, invalidCommentDto));
    }

    @Test
    void testCreateComment_ThrowsExceptionWhenContentIsNull() {
        CommentDto invalidCommentDto = CommentDto.builder()
                .authorId(1L)
                .postId(1L)
                .content(null)
                .build();

        assertThrows(CommentValidationException.class, () -> commentService.createComment(1L, invalidCommentDto));
    }

    @Test
    void testUpdateComment_Success() {
        comment.setPost(post);

        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));
        when(commentRepository.save(comment)).thenReturn(comment);
        when(commentMapper.toDto(comment)).thenReturn(commentDto);

        CommentDto updatedCommentDto = commentService.updateComment(1L, 1L, commentDto);

        verify(commentRepository, times(1)).save(comment);

        assertNotNull(updatedCommentDto, "The updatedCommentDto should not be null");
        assertEquals(commentDto, updatedCommentDto, "The returned DTO should match the expected DTO");


    }

    @Test
    void testUpdateComment_ThrowsExceptionWhenCommentDtoIsNull() {
        assertThrows(CommentValidationException.class, () -> commentService.updateComment(1L,
                1L, null));
    }

    @Test
    void testUpdateComment_ThrowsExceptionWhenAuthorIsNotTheSame() {
        comment.setPost(post);

        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));

        CommentDto invalidCommentDto = CommentDto.builder()
                .id(1L)
                .content("Updated comment")
                .authorId(2L)
                .postId(1L)
                .largeImageFileKey("large-key")
                .smallImageFileKey("small-key")
                .build();

        assertThrows(CommentValidationException.class, ()
                -> commentService.updateComment(1L, 1L, invalidCommentDto));

        verify(commentRepository, never()).save(any());
        verify(commentMapper, never()).toDto(any());
    }

    @Test
    void testGetCommentsByPostId_Success() {
        List<Comment> comments = List.of(comment);
        when(commentRepository.findAllByPostId(1L)).thenReturn(comments);
        when(commentMapper.toDto(comment)).thenReturn(commentDto);

        List<CommentDto> result = commentService.getCommentsByPostId(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(commentDto, result.get(0));
    }

    @Test
    void testDeleteComment_Success() {
        comment.setPost(post);

        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));

        commentService.deleteComment(1L, 1L);

        verify(commentRepository, times(1)).deleteById(1L);

    }

    @Test
    void testDeleteComment_ThrowsExceptionWhenCommentNotFound() {
        when(commentRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> commentService.deleteComment(1L, 1L));
    }

    @Test
    void testValidateCommentAndPost_Success() {
        comment.setPost(post);

        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));

        Comment result = commentService.validateCommentAndPost(1L, 1L);

        assertNotNull(result, "The result should not be null");
        assertEquals(comment, result, "The returned comment should match the expected comment");
    }

    @Test
    void testValidateCommentAndPost_ThrowsExceptionWhenCommentNotFound() {
        when(commentRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, ()
                -> commentService.validateCommentAndPost(1L, 1L));
    }

    @Test
    void testValidateCommentAndPost_ThrowsExceptionWhenCommentDoesNotBelongToPost() {
        Post anotherPost = Post.builder()
                .id(2L)
                .build();

        comment.setPost(anotherPost);

        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));

        assertThrows(CommentValidationException.class, ()
                -> commentService.validateCommentAndPost(1L, 1L));
    }

    @Test
    void testValidateCommentAndPost_ThrowsExceptionWhenPostIsNull() {
        comment.setPost(null);
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));

        assertThrows(CommentValidationException.class, ()
                -> commentService.validateCommentAndPost(1L, 1L));
    }
}