package faang.school.postservice.dictionary;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ModerationDictionaryTest {
    @Autowired
    private ModerationDictionary moderationDictionary;

    @Test
    void containsUnwantedWords() {
        assertTrue(moderationDictionary.containsUnwantedWords("This comment contains nigger"));
        assertFalse(moderationDictionary.containsUnwantedWords("This comment is clean"));
    }
}