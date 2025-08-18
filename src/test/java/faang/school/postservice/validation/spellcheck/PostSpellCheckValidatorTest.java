package faang.school.postservice.validation.spellcheck;

import faang.school.postservice.model.Post;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PostSpellCheckValidatorTest {

    private final PostSpellCheckValidator validator = new PostSpellCheckValidator();
    private Post post;

    @BeforeEach
    void setUp() {
        post = new Post();
    }

    @Test
    @DisplayName("Should return false for null content")
    void shouldReturnFalseForNullContent() {
        post.setContent(null);
        assertFalse(validator.hasValidText(post));
    }

    @Test
    @DisplayName("Should return false for blank content")
    void shouldReturnFalseForBlankContent() {
        post.setContent("   ");
        assertFalse(validator.hasValidText(post));
    }

    @Test
    @DisplayName("Should return true for non-blank content")
    void shouldReturnTrueForValidContent() {
        post.setContent("Hello world!");
        assertTrue(validator.hasValidText(post));
    }

    @Test
    @DisplayName("Should detect unchanged content")
    void shouldDetectUnchangedContent() {
        post.setContent("Hello");
        assertFalse(validator.isContentChanged(post, "Hello"));
    }

    @Test
    @DisplayName("Should detect changed content")
    void shouldDetectChangedContent() {
        post.setContent("Hello");
        assertTrue(validator.isContentChanged(post, "Hello corrected"));
    }
}
