package faang.school.postservice;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.comment.UpdateCommentDto;
import faang.school.postservice.exceptions.DataValidationException;
import faang.school.postservice.mapper.comment.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.service.CommentService;
import faang.school.postservice.validator.CommentValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CommentServiceTest {
    private static final long USER_ID = 1L;
    private static final long POST_ID = 2L;
    private static final long COMMENT_ID = 1L;
    private static final String CONTENT = "Test Content";
    private static final String UPDATED_CONTENT = "Updated Content";
    @Mock
    private CommentValidator commentValidator;
    @Mock
    private CommentRepository commentRepository;
    @Spy
    private CommentMapper commentMapper = Mappers.getMapper(CommentMapper.class);

    @InjectMocks
    private CommentService commentService;

    CommentDto commentDto;
    UpdateCommentDto updatedCommentDto;
    Comment firstComment;
    Comment secondComment;
    List<Comment> comments;

    @BeforeEach
    void init() {
        commentDto = CommentDto.builder()
                .id(COMMENT_ID)
                .postId(POST_ID)
                .content(CONTENT)
                .authorId(USER_ID)
                .createdAt(LocalDateTime.now())
                .build();

        updatedCommentDto = UpdateCommentDto.builder()
                .id(COMMENT_ID)
                .content(UPDATED_CONTENT)
                .build();

        firstComment = new Comment();
        firstComment.setId(1L);
        firstComment.setContent("First");
        firstComment.setCreatedAt(LocalDateTime.now());
        secondComment = new Comment();
        secondComment.setId(2L);
        secondComment.setContent("Second");
        secondComment.setCreatedAt(LocalDateTime.now().minusDays(5));

        comments = List.of(firstComment, secondComment);

    }

    @Test
    @DisplayName("Test delete comment : Wrong ID")
    public void testDeleteCommentWrongID() {
        String errorMessage = "Comment doesn't exist in the system ID = " + COMMENT_ID;
        doThrow(new DataValidationException(errorMessage)).when(commentValidator).checkCommentIsExist(COMMENT_ID);

        Exception exception = assertThrows(DataValidationException.class, () -> commentService.delete(COMMENT_ID));

        assertEquals(errorMessage, exception.getMessage());
    }

    @Test
    @DisplayName("Test delete comment : Successfully")
    public void testDeleteOk() {
        doNothing().when(commentValidator).checkCommentIsExist(COMMENT_ID);

        commentService.delete(COMMENT_ID);
        verify(commentRepository, times(1)).deleteById(COMMENT_ID);
    }

    @Test
    @DisplayName("Test get all comments by post id : Wrong post ID")
    public void getAllCommentsByPostIdWrongId() {
        String errorMessage = "Post doesn't exist in the system ID = " + POST_ID;
        doThrow(new DataValidationException(errorMessage)).when(commentValidator).checkPostIsExist(POST_ID);

        Exception exception = assertThrows(DataValidationException.class, () -> commentService.getAllCommentsByPostId(POST_ID));

        assertEquals(errorMessage, exception.getMessage());
        verifyNoMoreInteractions(commentRepository);
    }

    @Test
    @DisplayName("Test get all comments by post id : Everything is ok")
    public void getAllCommentsByPostIValidId() {
        doNothing().when(commentValidator).checkPostIsExist(POST_ID);
        when(commentRepository.findAllByPostId(POST_ID)).thenReturn(comments);

        List<CommentDto> result = commentService.getAllCommentsByPostId(POST_ID);

        assertEquals(2, result.size());
        assertEquals("Second", result.get(0).getContent());
    }

    @Test
    @DisplayName("Test create comment : Validation failed")
    public void testCreateValidationFailed() {
        String errorMessage = "User doesn't exist in the system ID = " + USER_ID;
        doThrow(new DataValidationException(errorMessage)).when(commentValidator).checkUserIsExist(USER_ID);
        Exception exception = assertThrows(DataValidationException.class, () -> commentService.createComment(commentDto));

        assertEquals(errorMessage, exception.getMessage());
        verifyNoMoreInteractions(commentValidator, commentRepository);
    }

    @Test
    @DisplayName("Test create comment : Validation passed")
    public void testCreateOk() {
        doNothing().when(commentValidator).checkUserIsExist(USER_ID);
        doNothing().when(commentValidator).checkPostIsExist(POST_ID);

        Comment comment = commentMapper.toEntity(commentDto);
        when(commentRepository.save(comment)).thenReturn(comment);

        CommentDto result = commentService.createComment(commentDto);
        assertEquals(result.getId(), commentDto.getId());
    }

}
