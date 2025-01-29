package faang.school.postservice.service;

import faang.school.postservice.dto.comment.CommentResponse;
import faang.school.postservice.dto.comment.CommentUpdateRequest;
import faang.school.postservice.dto.comment.CreateCommentRequest;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock
    private ValidateService validateService;

    @Spy
    private CommentMapper commentMapper = Mappers.getMapper(CommentMapper.class);

    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private CommentService commentService;

    @Test
    void create_Success() {
        CreateCommentRequest request = new CreateCommentRequest(1L, "Текст", 2L);
        Post post = new Post();
        post.setId(2L);

        Comment commentSaved = Comment.builder()
                .authorId(1L)
                .id(1L)
                .content("Текст")
                .post(post)
                .build();

        doNothing().when(validateService).validateUser(request.userId());
        doNothing().when(validateService).validatePost(request.postId());

        when(commentRepository.save(any(Comment.class))).thenReturn(commentSaved);

        CommentResponse actualResponse = commentService.create(request);

        assertNotNull(actualResponse);
        assertEquals("Текст", actualResponse.content());

        verify(validateService).validateUser(1L);
        verify(validateService).validatePost(2L);
        verify(commentMapper).toEntity(request);
        verify(commentRepository).save(any(Comment.class));
        verify(commentMapper).toCommentResponse(commentSaved);
    }

    @Test
    void update_ShouldThrowException_WhenCommentNotFound() {
        CommentUpdateRequest request = new CommentUpdateRequest(999L, "Текст");

        when(commentRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> commentService.update(request));
        verify(commentRepository).findById(999L);
    }

    @Test
    void getAllByPostId_ShouldReturnSortedComments_WhenPostIdIsValid() {
        long postId = 2L;
        Comment c1 = new Comment();
        c1.setId(1L);
        c1.setCreatedAt(LocalDateTime.now().minusHours(2));

        Comment c2 = new Comment();
        c2.setId(2L);
        c2.setCreatedAt(LocalDateTime.now().minusHours(1));

        List<Comment> comments = List.of(c1, c2);

        when(commentRepository.findAllByPostId(postId)).thenReturn(comments);

        // when
        List<CommentResponse> actualList = commentService.getAllByPostId(postId);

        // then
        assertEquals(2, actualList.size());
        // Проверяем, что сортировка по убыванию даты:
        assertEquals(2L, actualList.get(0).id());
        assertEquals(1L, actualList.get(1).id());

        verify(commentRepository).findAllByPostId(postId);
        verify(commentMapper, times(2)).toCommentResponse(any(Comment.class));
    }

    @Test
    void delete_ShouldDeleteComment_WhenCommentExists() {
        Long commentId = 2L;
        when(commentRepository.existsById(commentId)).thenReturn(true);

        commentService.delete(commentId);

        verify(commentRepository).existsById(commentId);
        verify(commentRepository).deleteById(commentId);
    }

    @Test
    void delete_ShouldThrowException_WhenCommentDoesNotExist() {
        Long commentId = 2L;
        when(commentRepository.existsById(commentId)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> commentService.delete(commentId));
        verify(commentRepository).existsById(commentId);
        verify(commentRepository, never()).deleteById(any());
    }
}