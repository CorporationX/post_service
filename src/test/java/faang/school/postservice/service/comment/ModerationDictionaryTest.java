package faang.school.postservice.service.comment;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ModerationDictionaryTest {

    private ModerationDictionary moderationDictionary;

    @BeforeEach
    public void setup() {
        moderationDictionary = new ModerationDictionary();
        ReflectionTestUtils.setField(moderationDictionary, "wordSplitRegex", "\\W+");

        Set<String> badWords = Set.of("badword", "forbidden");
        ReflectionTestUtils.setField(moderationDictionary, "badWords", badWords);
    }

    @Test
    public void testContainsForbiddenWords_Found() {
        assertTrue(moderationDictionary.containsForbiddenWords("This is a badword test."));
    }

    @Test
    public void testContainsForbiddenWords_NotFound() {
        assertFalse(moderationDictionary.containsForbiddenWords("This is a clean test."));
    }
}
