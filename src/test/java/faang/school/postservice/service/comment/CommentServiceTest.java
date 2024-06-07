package faang.school.postservice.service.comment;

import faang.school.postservice.dto.comment.ChangeCommentDto;
import faang.school.postservice.dto.comment.CreateCommentDto;
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

    private CreateCommentDto createCommentDto;
    private ChangeCommentDto changeCommentDto;
    private Comment comment;
    private Long id;

    @BeforeEach
    public void setUp() {
        Post post = Post.builder().id(1L).build();
        createCommentDto = CreateCommentDto.builder().id(1L).content("content").authorId(1L).postId(1L).build();
        changeCommentDto = ChangeCommentDto.builder().id(1L).content("content").build();
        comment = Comment.builder().id(1L).content("content").authorId(1L).post(post).build();
        id = 1L;
    }

    @Test
    public void testCorrectWorkCreateComment() {
        when(commentMapper.toEntity(createCommentDto)).thenReturn(comment);
        when(commentRepository.save(comment)).thenReturn(comment);
        when(commentMapper.toDto(comment)).thenReturn(createCommentDto);

        CreateCommentDto result = commentService.createComment(createCommentDto);

        assertEquals(createCommentDto, result);
        verify(commentMapper).toEntity(createCommentDto);
        verify(commentRepository).save(comment);
        verify(commentMapper).toDto(comment);
    }

    @Test
    public void testCorrectWorkChangeComment() {
        when(commentRepository.findById(createCommentDto.getId())).thenReturn(Optional.ofNullable(comment));

        when(commentMapper.toDto(comment)).thenReturn(createCommentDto);

        CreateCommentDto result = commentService.changeComment(changeCommentDto);

        assertEquals(createCommentDto, result);
        verify(commentRepository).findById(createCommentDto.getId());
        verify(commentMapper).toDto(comment);
    }

    @Test
    public void testChangeCommentWithValidationException() {
        when(commentRepository.findById(createCommentDto.getId())).thenThrow(DataValidationException.class);
        assertThrows(DataValidationException.class, () -> commentService.changeComment(changeCommentDto));
    }


    @Test
    public void testCorrectWorkGetAllCommentsOnPostId() {
        when(commentRepository.findAllByPostIdOrderByCreatedAtDesc(id)).thenReturn(Collections.singletonList(comment));
        doNothing().when(commentValidator).getAllCommentsOnPostIdService(id);
        when(commentMapper.toDto(comment)).thenReturn(createCommentDto);

        List<CreateCommentDto> result = commentService.getAllCommentsOnPostId(id);

        verify(commentValidator, times(1)).getAllCommentsOnPostIdService(createCommentDto.getPostId());
        verify(commentRepository, times(1)).findAllByPostIdOrderByCreatedAtDesc(createCommentDto.getPostId());
        verify(commentMapper, times(1)).toDto(comment);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertNotNull(result.get(0));
        assertEquals(id, result.get(0).getId());
        assertEquals(createCommentDto.getContent(), result.get(0).getContent());
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
