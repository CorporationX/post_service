package faang.school.postservice.dictionary;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ModerationDictionaryTest {

    private ModerationDictionary dictionary;

    @BeforeEach
    void setUp() {
        dictionary = getModerationDictionary();
    }

    @Test
    void testHasOffensiveWordsWhenTextContainsOffensiveWordShouldReturnTrue() {
        assertTrue(dictionary.hasOffensiveWords("rugaga"));
    }

    @Test
    void testHasOffensiveWordsWhenTextDoesNotContainOffensiveWordsShouldReturnFalse() {
        assertFalse(dictionary.hasOffensiveWords("This is a clean word example"));
    }

    @Test
    void testHasOffensiveWords_WhenTextIsEmpty_ShouldReturnFalse() {
        assertFalse(dictionary.hasOffensiveWords(""));
    }

    @Test
    void testHasOffensiveWords_WhenTextIsNull_ShouldReturnFalse() {
        assertFalse(dictionary.hasOffensiveWords(null));
    }

    private ModerationDictionary getModerationDictionary() {
        List<String> offensiveWords = List.of("rugaga", "fgs", "agbz");
        return new ModerationDictionary("dummy-path") {
            @Override
            public List<String> loadWordsFromFile(String filePath) {
                return offensiveWords;
            }
        };
    }
}