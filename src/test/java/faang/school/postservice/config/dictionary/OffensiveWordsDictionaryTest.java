package faang.school.postservice.config.dictionary;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.PropertySource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
@PropertySource("classpath:application.properties")
class OffensiveWordsDictionaryTest {

    private OffensiveWordsDictionary offensiveWordsDictionary;

    private final String GOOD_WORD = "TEST";
    private final String BAD_WORD_ONE = "TEST1";
    private final String BAD_WORD_TWO = "TEST2";

    private final List<String> INITIAL_WORDS = List.of(BAD_WORD_ONE, BAD_WORD_TWO);

    @BeforeEach
    public void setUp() {
        offensiveWordsDictionary = new OffensiveWordsDictionary(INITIAL_WORDS);
    }

    @Test
    @DisplayName("If word contains in dictionary then return true")
    public void whenWordContainsInDictionaryThenReturnTrue() {
        assertTrue(offensiveWordsDictionary.isWordContainsInDictionary(BAD_WORD_ONE));
    }

    @Test
    @DisplayName("If word not contains in dictionary then return false")
    public void whenWordContainsInDictionaryThenReturnFalse() {
        assertFalse(offensiveWordsDictionary.isWordContainsInDictionary(GOOD_WORD));
    }

    @Test
    @DisplayName("When add new words in dictionary then they contains in dictionary")
    public void whenAddNewWordThenTheyAddCorrectly() {
        String word = "WORD";
        String anotherWord = "ANOTHER";
        String wordNotContains = "NOT";

        offensiveWordsDictionary.addWordsToDictionary(List.of(word, anotherWord));

        assertTrue(offensiveWordsDictionary.isWordContainsInDictionary(BAD_WORD_ONE));
        assertTrue(offensiveWordsDictionary.isWordContainsInDictionary(BAD_WORD_TWO));
        assertTrue(offensiveWordsDictionary.isWordContainsInDictionary(word));
        assertTrue(offensiveWordsDictionary.isWordContainsInDictionary(anotherWord));
        assertFalse(offensiveWordsDictionary.isWordContainsInDictionary(wordNotContains));
    }
}
