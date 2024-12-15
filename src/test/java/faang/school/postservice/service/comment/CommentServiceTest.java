package faang.school.postservice.service.comment;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.mapper.comment.CommentMapperImpl;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.publisher.CommentEventPublisher;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.service.post.PostService;
import faang.school.postservice.validator.comment.CommentValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {
    @Mock
    CommentEventPublisher commentEventPublisher;
    @Mock
    CommentValidator commentValidator;
    @Mock
    PostService postService;
    @Mock
    CommentRepository commentRepository;
    @Mock
    CommentMapperImpl commentMapper;
    @InjectMocks
    CommentService commentService;

    @Test
    void testCreate() {
        CommentDto commentDto = CommentDto.builder().authorId(1L).id(1L).content("1234").createdAt(LocalDateTime.now()).postId(1L).build();
        Mockito.when(postService.findEntityById(anyLong())).thenReturn(new Post());
        Comment comment = Comment.builder().id(1L).build();
        Mockito.when(commentMapper.toEntity(any())).thenReturn(comment);
        when(commentRepository.save(any())).thenReturn(comment);

        commentService.createComment(commentDto);

        verify(commentEventPublisher, times(1)).publish(any());
        Mockito.verify(commentRepository, times(1)).save(any());
    }

    @Test
    void testCreateFailedValidation() {
        CommentDto commentDto = CommentDto.builder().content("1234").postId(1L).build();
        Mockito.doThrow(new DataValidationException("fail")).when(commentValidator).validateCreation(any());

        assertThrows(DataValidationException.class, () -> commentService.createComment(commentDto));
        Mockito.verify(commentRepository, times(0)).save(any());
    }

    @Test
    void testCreatePostNotFound() {
        CommentDto commentDto = CommentDto.builder().content("1234").postId(1L).build();
        Mockito.when(postService.findEntityById(anyLong())).thenThrow(new DataValidationException("fail"));

        assertThrows(DataValidationException.class, () -> commentService.createComment(commentDto));
        Mockito.verify(commentRepository, times(0)).save(any());
    }


    @Test
    void testUpdateComment() {
        CommentDto commentDto = CommentDto.builder().id(1L).build();
        Mockito.when(commentRepository.findById(anyLong())).thenReturn(Optional.of(new Comment()));
        commentService.updateComment(commentDto);
        Mockito.verify(commentRepository, times(1)).save(any());
    }

    @Test
    void testUpdateCommentNotValid() {
        CommentDto commentDto = CommentDto.builder().build();

        assertThrows(DataValidationException.class, () -> commentService.updateComment(commentDto));
        Mockito.verify(commentRepository, times(0)).save(any());
    }

    @Test
    void testUpdateCommentValidationFail() {
        CommentDto commentDto = CommentDto.builder().id(1L).build();
        Mockito.when(commentRepository.findById(anyLong())).thenReturn(Optional.of(new Comment()));
        Mockito.doThrow(new DataValidationException("fail")).when(commentValidator).validateUpdate(any(), any());

        assertThrows(DataValidationException.class, () -> commentService.updateComment(commentDto));
        Mockito.verify(commentRepository, times(0)).save(any());
    }

    @Test
    void testGetComments() {
        Comment comment1 = new Comment();
        comment1.setId(1L);
        comment1.setCreatedAt(LocalDateTime.now().minusDays(2));
        Comment comment2 = new Comment();
        comment2.setId(2L);
        comment2.setCreatedAt(LocalDateTime.now().minusDays(1));
        Comment comment3 = new Comment();
        comment3.setId(3L);
        comment3.setCreatedAt(LocalDateTime.now().minusDays(3));

        List<Comment> comments = List.of(comment1, comment2, comment3);
        Mockito.when(commentRepository.findAllByPostId(anyLong())).thenReturn(comments);

        Mockito.when(commentMapper.toDto(comment1)).thenReturn(CommentDto.builder().id(comment1.getId()).build());
        Mockito.when(commentMapper.toDto(comment2)).thenReturn(CommentDto.builder().id(comment2.getId()).build());
        Mockito.when(commentMapper.toDto(comment3)).thenReturn(CommentDto.builder().id(comment3.getId()).build());

        List<CommentDto> foundedComments = commentService.getComments(1);

        assertAll(
                () -> assertEquals(3, foundedComments.size()),
                () -> assertEquals(3, foundedComments.get(0).getId()),
                () -> assertEquals(1, foundedComments.get(1).getId()),
                () -> assertEquals(2, foundedComments.get(2).getId())
        );
    }

    @Test
    void testDelete() {
        Mockito.doNothing().when(commentRepository).deleteById(anyLong());
        assertDoesNotThrow(() -> commentService.deleteComment(1));
    }
}
