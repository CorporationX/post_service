package faang.school.postservice.service.comment;

import faang.school.postservice.dto.comment.CommentEvent;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.exception.EntityWrongParameterException;
import faang.school.postservice.exception.NoAccessException;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.publisher.CommentEventPublisher;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.service.post.PostService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    private CommentDto commentDto;
    private Comment comment;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private PostService postService;

    @Mock
    private CommentEventPublisher commentEventPublisher;

    @Mock
    private CommentMapper commentMapper;

    @InjectMocks
    private CommentService commentService;

    @BeforeEach
    void setUp() {
        Post post = Post.builder()
                .id(3L)
                .content("Post")
                .build();

        commentDto = CommentDto.builder()
                .id(1L)
                .content("Comment")
                .authorId(2L)
                .postId(3L)
                .createdAt(LocalDateTime.now())
                .build();

        comment = Comment.builder()
                .id(1L)
                .content("Comment")
                .authorId(2L)
                .post(post)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    void createCommentValidCommentDtoShouldSaveCommentAndReturnCommentDto() {
        when(commentMapper.fromDto(commentDto)).thenReturn(comment);
        when(commentRepository.save(comment)).thenReturn(comment);
        when(commentMapper.toDto(comment)).thenReturn(commentDto);

        CommentEvent expectedEvent = CommentEvent.builder()
                .commentAuthorId(comment.getAuthorId())
                .postAuthorId(comment.getPost().getAuthorId())
                .postId(comment.getPost().getId())
                .commentId(comment.getId())
                .build();

        when(commentMapper.toCommentEvent(comment)).thenReturn(expectedEvent);

        CommentDto result = commentService.createComment(commentDto);
        assertEquals(commentDto, result);
        verify(commentMapper).fromDto(commentDto);
        verify(commentRepository).save(comment);
        verify(commentMapper).toDto(comment);
        verify(commentMapper).toCommentEvent(comment);
        verify(commentEventPublisher).publish(expectedEvent);
    }

    @Test
    void updateCommentValidCommentDtoShouldUpdateCommentAndReturnCommentDto() {
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));
        when(commentMapper.toDto(comment)).thenReturn(commentDto);
        when(commentRepository.save(comment)).thenReturn(comment);
        CommentDto result = commentService.updateComment(1L, commentDto);
        assertEquals(commentDto, result);
        verify(commentRepository, times(1)).findById(1L);
        verify(commentRepository, times(1)).save(comment);
        verify(commentMapper, times(1)).toDto(comment);
    }

    @Test
    void updateCommentCommentNotFoundShouldThrowEntityNotFoundException() {
        when(commentRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> commentService.updateComment(1L, commentDto));
        verify(commentRepository, times(1)).findById(1L);
        verify(commentRepository, never()).save(comment);
        verify(commentMapper, never()).toDto(comment);
    }

    @Test
    void updateCommentDifferentAuthorShouldThrowEntityWrongParameterException() {
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));
        commentDto.setAuthorId(3L);
        assertThrows(EntityWrongParameterException.class, () -> commentService.updateComment(1L, commentDto));
        verify(commentRepository, times(1)).findById(1L);
        verify(commentRepository, never()).save(comment);
        verify(commentMapper, never()).toDto(comment);
    }

    @Test
    void getAllCommentsForPostValidPostIdShouldReturnSortedCommentDto() {
        List<Comment> comments = new ArrayList<>();
        comments.add(comment);
        when(commentRepository.findAllByPostId(3L)).thenReturn(comments);
        when(commentMapper.toDto(comment)).thenReturn(commentDto);
        List<CommentDto> result = commentService.getAllCommentsForPost(3L);
        assertEquals(1, result.size());
        assertEquals(commentDto, result.get(0));
        verify(commentRepository, times(1)).findAllByPostId(3L);
        verify(commentMapper, times(1)).toDto(comment);
    }

    @Test
    void getAllCommentsForPostInvalidPostIdShouldThrowEntityNotFoundException() {
        when(postService.getPostById(3L)).thenThrow(new EntityNotFoundException("Пост с id 3 не найден"));
        assertThrows(EntityNotFoundException.class, () -> commentService.getAllCommentsForPost(3L));
        verify(postService, times(1)).getPostById(3L);
        verify(commentRepository, never()).findAllByPostId(3L);
        verify(commentMapper, never()).toDto(comment);
    }

    @Test
    void getAllCommentsForPostNoCommentsForPostShouldReturnEmptyList() {
        List<Comment> emptyComments = new ArrayList<>();
        when(commentRepository.findAllByPostId(3L)).thenReturn(emptyComments);
        List<CommentDto> result = commentService.getAllCommentsForPost(3L);
        assertTrue(result.isEmpty());
        verify(commentRepository, times(1)).findAllByPostId(3L);
        verify(commentMapper, never()).toDto(comment);
    }

    @Test
    void deleteCommentValidCommentIdAndUserIdShouldDeleteComment() {
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));
        commentService.deleteComment(1L, 2L);
        verify(commentRepository, times(1)).deleteById(1L);
        verify(commentRepository, times(1)).findById(1L);
    }

    @Test
    void deleteCommentCommentNotFoundShouldThrowEntityNotFoundException() {
        when(commentRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> commentService.deleteComment(1L, 2L));
        verify(commentRepository, times(1)).findById(1L);
        verify(commentRepository, never()).deleteById(1L);
    }

    @Test
    void deleteCommentInvalidUserIdShouldThrowNoAccessException() {
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));
        assertThrows(NoAccessException.class, () -> commentService.deleteComment(1L, 3L));
        verify(commentRepository, times(1)).findById(1L);
        verify(commentRepository, never()).deleteById(1L);
    }

    @Test
    void whenExistsByIdThenTrue() {
        when(commentRepository.existsById(anyLong())).thenReturn(true);
        assertTrue(commentService.existsById(1));
    }

    @Test
    void whenExistsByIdThenFalse() {
        when(commentRepository.existsById(anyLong())).thenReturn(false);
        assertFalse(commentService.existsById(1));
    }
}