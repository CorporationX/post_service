package faang.school.postservice.util;

import faang.school.postservice.model.Post;
import faang.school.postservice.config.ObsceneWordProperties;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ModerationDictionaryTest {

    @InjectMocks
    private ModerationDictionary moderationDictionary;

    @Mock
    private ObsceneWordProperties obsceneWordProperties;

    private final Post post = new Post();

    @Test
    void testIsVerified_WithObsceneWord_ShouldReturnFalse() {
        post.setContent("This content contains badword");
        Set<String> obsceneWords = Set.of("badword");
        when(obsceneWordProperties.getWords()).thenReturn(obsceneWords);

        boolean result = moderationDictionary.isVerified(post);

        assertFalse(result);
    }

    @Test
    void testIsVerified_WithoutObsceneWord_ShouldReturnTrue() {
        post.setContent("This content is clean");
        Set<String> obsceneWords = Set.of("badword");
        when(obsceneWordProperties.getWords()).thenReturn(obsceneWords);

        boolean result = moderationDictionary.isVerified(post);

        assertTrue(result);
    }

    @Test
    void testIsVerified_WithEmptyObsceneWords_ShouldReturnTrue() {
        post.setContent("This content is clean");
        Set<String> obsceneWords = Set.of();
        when(obsceneWordProperties.getWords()).thenReturn(obsceneWords);

        boolean result = moderationDictionary.isVerified(post);

        assertTrue(result);
    }

    @Test
    void testIsVerified_WithMultipleObsceneWords_ShouldReturnFalse() {
        post.setContent("This content contains anotherbadword");
        Set<String> obsceneWords = Set.of("badword", "anotherbadword");
        when(obsceneWordProperties.getWords()).thenReturn(obsceneWords);

        boolean result = moderationDictionary.isVerified(post);

        assertFalse(result);
    }
}
