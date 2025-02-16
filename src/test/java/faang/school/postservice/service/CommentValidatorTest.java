package faang.school.postservice.service;

import faang.school.postservice.model.Comment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class CommentValidatorTest {

    @Spy
    private CommentValidator commentValidator;

    private Comment comment;

    @BeforeEach
    public void setUp() {
        comment = Comment.builder()
                .content("content")
                .build();
    }

    @Test
    public void validateCommentUpdateTest() {
        assertDoesNotThrow(() ->
                commentValidator.validateCommentUpdate(comment));
    }

    @Test
    public void validateCommentUpdateTest_modifiedCommentId() {
        comment.setId(1L);
        assertThrows(IllegalArgumentException.class, () ->
                commentValidator.validateCommentUpdate(comment));
    }

    @Test
    public void validateCommentUpdateTest_modifiedAuthorId() {
        comment.setAuthorId(2L);
        assertThrows(IllegalArgumentException.class, () ->
                commentValidator.validateCommentUpdate(comment));
    }

    @Test
    public void validateCommentUpdateTest_modifiedLargeImageKey() {
        comment.setLargeImageFileKey("222");
        assertThrows(IllegalArgumentException.class, () ->
                commentValidator.validateCommentUpdate(comment));
    }

    @Test
    public void validateCommentUpdateTest_modifiedSmallImageKey() {
        comment.setSmallImageFileKey("444");
        assertThrows(IllegalArgumentException.class, () ->
                commentValidator.validateCommentUpdate(comment));
    }

    @Test
    public void validateAuthorTest() {
        comment.setAuthorId(1L);
        assertDoesNotThrow(() ->
                commentValidator.validateAuthor(comment, 1L));

    }

    @Test
    public void validateAuthor_differentUserIds() {
        comment.setAuthorId(2L);
        assertThrows(IllegalArgumentException.class, () ->
                commentValidator.validateAuthor(comment, 1L));
    }
}
