package faang.school.postservice.moderation;

import org.junit.jupiter.api.Test;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
class ModerationDictionaryTest {
    ModerationDictionary dictionary = new ModerationDictionary(List.of("badword", "testmat"));

    @Test
    void shouldDetectBadWordsInText() {
        assertTrue(dictionary.containsBadWords("This contains badword."));
        assertTrue(dictionary.containsBadWords("clean TESTMAT text"));
        assertFalse(dictionary.containsBadWords("This is fine."));
        assertFalse(dictionary.containsBadWords(null));
        assertFalse(dictionary.containsBadWords(""));
    }
}