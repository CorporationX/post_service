package faang.school.postservice.service.comment;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.exeption.DataValidationException;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.service.post.PostService;
import faang.school.postservice.validator.comment.CommentValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class CommentServiceTest {
    @InjectMocks
    private CommentService commentService;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private PostService postService;
    @Mock
    private CommentValidator commentValidator;
    private long rightId;
    private long wrongId;
    private Comment comment;
    private Post post;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        rightId = 1L;
        wrongId = -2L;
        comment = Comment.builder()
                .content("First message")
                .build();
        Mockito.when(commentRepository.findById(rightId))
                .thenReturn(Optional.of(comment));
        Mockito.when(postService.getPostById(rightId))
                .thenReturn(post);
    }

    @Test
    void createComment() {
    }

    @Test
    void testUpdateComment() { //uses or overrides a deprecated API.
        String content = "Second message";
        CommentDto commentDtoRight = CommentDto.builder()
                .id(rightId)
                .postId(rightId)
                .content(content)
                .build();

        commentService.updateComment(commentDtoRight);
        Mockito.verify(commentRepository, Mockito.times(1))
                .findById(rightId);
        Mockito.verify(postService, Mockito.times(1))
                .getPostById(rightId);
        Mockito.verify(commentValidator, Mockito.times(1))
                .validateUpdateComment(post, comment);
        assertEquals(content, comment.getContent());

        CommentDto commentDtoFalse = CommentDto.builder()
                .id(wrongId)
                .postId(wrongId)
                .build();

        assertThrows(DataValidationException.class,
                () -> commentService.updateComment(commentDtoFalse));
    }

    @Test
    void getAllComments() {
    }

    @Test
    void deleteComment() {
    }
}