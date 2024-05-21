package faang.school.postservice.service.comment;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.mapper.comment.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.validator.CommentValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CommentServiceTest {

    @InjectMocks
    private CommentService commentService;

    @Spy
    private CommentMapper commentMapper;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private CommentValidator commentValidator;

    private CommentDto commentDto;
    private Comment comment;
    private Long id;

    @BeforeEach
    public void setUp() {
        Post post = Post.builder().id(1L).build();
        commentDto = CommentDto.builder().id(1L).content("content").authorId(1L).postId(1L).build();
        comment = Comment.builder().id(1L).content("content").authorId(1L).post(post).build();
        id = 1L;
    }

    @Test
    public void testCorrectWorkCreateComment() {
        when(commentMapper.toEntity(commentDto)).thenReturn(comment);
        when(commentRepository.save(comment)).thenReturn(comment);
        when(commentMapper.toDto(comment)).thenReturn(commentDto);

        CommentDto result = commentService.createComment(commentDto);

        assertEquals(commentDto, result);
        verify(commentMapper).toEntity(commentDto);
        verify(commentRepository).save(comment);
        verify(commentMapper).toDto(comment);
    }

    @Test
    public void testCorrectWorkChangeComment() {
        when(commentRepository.findById(commentDto.getId())).thenReturn(Optional.ofNullable(comment));

        when(commentMapper.toDto(comment)).thenReturn(commentDto);

        CommentDto result = commentService.changeComment(commentDto);

        assertEquals(commentDto, result);
        verify(commentRepository).findById(commentDto.getId());
        verify(commentMapper).toDto(comment);
    }

    @Test
    public void testChangeCommentWithValidationException() {
        when(commentRepository.findById(commentDto.getId())).thenThrow(DataValidationException.class);
        assertThrows(DataValidationException.class, () -> commentService.changeComment(commentDto));
    }


    @Test
    public void testCorrectWorkGetAllCommentsOnPostId() {
        when(commentRepository.findAllByPostId(id)).thenReturn(Collections.singletonList(comment));
        doNothing().when(commentValidator).getAllCommentsOnPostIdService(id);
        when(commentMapper.toDto(comment)).thenReturn(commentDto);

        List<CommentDto> result = commentService.getAllCommentsOnPostId(id);

        verify(commentValidator, times(1)).getAllCommentsOnPostIdService(commentDto.getPostId());
        verify(commentRepository, times(1)).findAllByPostId(commentDto.getPostId());
        verify(commentMapper, times(1)).toDto(comment);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertNotNull(result.get(0));
        assertEquals(id, result.get(0).getId());
        assertEquals(commentDto.getContent(), result.get(0).getContent());
    }

    @Test
    public void testGetAllCommentsOnPostIdWithValidationException() {
        doThrow(DataValidationException.class).when(commentValidator).getAllCommentsOnPostIdService(id);
        assertThrows(DataValidationException.class, () -> commentService.getAllCommentsOnPostId(id));
    }

    @Test
    public void testCorrectWorkDeleteComment() {
        doNothing().when(commentRepository).deleteById(id);
        commentService.deleteComment(id);
        verify(commentRepository, times(1)).deleteById(id);
    }
}
