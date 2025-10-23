package faang.school.postservice.util;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.service.CommentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;
import java.util.Optional;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Spy
    private CommentMapper commentMapper;

    @InjectMocks
    private CommentService commentService;

    @Test
    void saveCommentTest() {
        long commentId = 1L;
        CommentDto commentDto = new CommentDto();
        commentDto.setId(1L);

        Comment savedComment = new Comment();
        savedComment.setId(commentId);

        CommentDto result = commentService.save(commentDto);
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(savedComment));
        verify(commentRepository).save(any(Comment.class));

        assertNotNull(result);
        assertEquals(savedComment.getId(), result.getId());

    }

    @Test
    void getCommentsTest() {
        long postId = 1L;
        List<CommentDto> comments =  commentService.findAllByPostId(postId);
        verify(commentRepository).findAllByPostId(postId);
        assertNotNull(comments);
    }

    @Test
    void findByIdTest() {
        long commentId = 1L;
        Comment comment = new Comment();
        comment.setId(commentId);

        commentService.findById(commentId);
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

        CommentDto result = commentMapper.toDto(comment);

        verify(commentRepository).findById(commentId);
        verify(commentMapper).toDto(comment);

        assertNotNull(result);
        assertEquals(result.getId(), comment.getId());
    }

    @Test
    void deleteCommentTest() {
        long commentId = 1L;
        commentService.deleteById(commentId);
        verify(commentRepository).deleteById(commentId);
    }


}
