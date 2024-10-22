package faang.school.postservice.service.post;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class ModerationDictionaryTest {
    private ObjectMapper objectMapper;
    private ModerationDictionary moderationDictionary;
    private Field forbiddenWordsField;

    @BeforeEach
    void setUp() throws Exception {
        objectMapper = new ObjectMapper();
        moderationDictionary = new ModerationDictionary(objectMapper);
        forbiddenWordsField = ModerationDictionary.class.getDeclaredField("forbiddenWords");
        forbiddenWordsField.setAccessible(true);
        forbiddenWordsField.set(moderationDictionary, Set.of("badword", "anotherbadword"));
    }

    @Test
    void testLoadForbiddenWords_Success() throws Exception {
        forbiddenWordsField.set(moderationDictionary, null);

        moderationDictionary.loadForbiddenWords();

        Set<String> forbiddenWords = (Set<String>) forbiddenWordsField.get(moderationDictionary);

        assertNotNull(forbiddenWords);
        assertTrue(forbiddenWords.contains("badword"), "forbiddenWords не содержит 'badword'");
        assertTrue(forbiddenWords.contains("anotherbadword"), "forbiddenWords не содержит 'anotherbadword'");
    }

    @Test
    void testContainsForbiddenWord_ContainsWord() {
        assertTrue(moderationDictionary.containsForbiddenWord("This is a badword in the text."));
    }

    @Test
    void testContainsForbiddenWord_DoesNotContainWord() {
        assertFalse(moderationDictionary.containsForbiddenWord("This is a clean text."));
    }

    @Test
    void testContainsForbiddenWord_NullOrEmptyContent() {
        assertFalse(moderationDictionary.containsForbiddenWord(null));
        assertFalse(moderationDictionary.containsForbiddenWord(""));
    }
}