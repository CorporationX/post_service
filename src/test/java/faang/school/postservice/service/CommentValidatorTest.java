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
                .content("test content")
                .build();
    }

    @Test
    public void validateCommentUpdate_shouldPass_whenOnlyContentChanged() {
        assertDoesNotThrow(() -> commentValidator.validateCommentUpdate(comment));
    }

    @Test
    public void validateCommentUpdate_shouldThrow_whenIdModified() {
        comment.setId(1L);
        assertThrows(IllegalArgumentException.class, () ->
                commentValidator.validateCommentUpdate(comment));
    }

    @Test
    public void validateCommentUpdate_shouldThrow_whenAuthorIdModified() {
        comment.setAuthorId(2L);
        assertThrows(IllegalArgumentException.class, () ->
                commentValidator.validateCommentUpdate(comment));
    }

    @Test
    public void validateCommentUpdate_shouldThrow_whenPostModified() {
        comment.setPost(new faang.school.postservice.model.Post());
        assertThrows(IllegalArgumentException.class, () ->
                commentValidator.validateCommentUpdate(comment));
    }

    @Test
    public void validateAuthor_shouldPass_whenAuthorMatches() {
        comment.setAuthorId(1L);
        assertDoesNotThrow(() -> commentValidator.validateAuthor(comment, 1L));
    }

    @Test
    public void validateAuthor_shouldThrow_whenAuthorDiffers() {
        comment.setAuthorId(2L);
        assertThrows(IllegalArgumentException.class, () ->
                commentValidator.validateAuthor(comment, 1L));
    }
}
