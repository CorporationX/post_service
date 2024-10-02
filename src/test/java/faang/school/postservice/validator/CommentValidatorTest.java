package faang.school.postservice.validator;

import faang.school.postservice.dto.comment.UpdateCommentRequest;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class CommentValidatorTest {

    private final String content = "test";
    private final long authorId = 1L;
    private final LocalDateTime createdAt = LocalDateTime.of(2024, 9, 21, 11, 34, 54);
    private final LocalDateTime updatedAt = LocalDateTime.of(2024, 9, 24, 11, 30, 23);

    private UpdateCommentRequest updateCommentRequest;
    private Post post;


    @InjectMocks
    private CommentValidator commentValidator;

    @BeforeEach
    void init() {

        post = mock(Post.class);

        updateCommentRequest = UpdateCommentRequest.builder()
                .content(content)
                .authorId(authorId)
                .build();

    }

    @Test
    @DisplayName("successful validation")
    void testCheckingForComplianceWhenValidationSuccess() {
        Comment comment = Comment.builder()
                .id(1L)
                .content(content)
                .authorId(authorId)
                .post(post)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();

        commentValidator.checkingForCompliance(comment, updateCommentRequest);

        assertEquals(comment.getAuthorId(), updateCommentRequest.getAuthorId());
    }

    @Test
    @DisplayName("unsuccessful validation")
    void testCheckingForComplianceWhenValidationUnsuccessful() {
        Comment comment = Comment.builder()
                .id(1L)
                .content(content)
                .authorId(2L)
                .post(post)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();

        assertThrows(DataValidationException.class, () ->
                commentValidator.checkingForCompliance(comment, updateCommentRequest));
    }

}

