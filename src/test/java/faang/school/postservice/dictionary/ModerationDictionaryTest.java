package faang.school.postservice.dictionary;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;


import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class ModerationDictionaryTest {
    //TODO Не подтягивается файл, пока не понял, как пофиксить

    @InjectMocks
    private ModerationDictionary moderationDictionary;

    @BeforeEach
    void setUp() {}

    @ParameterizedTest
    @CsvSource({
            "This is a good text without any bad words.",
            "Another example without any inappropriate content.",
            "I love unit tests!"
    })
    void GoodTextTest(String content) {
        assertFalse(moderationDictionary.containsCensorWord(content));
    }

    @ParameterizedTest
    @CsvSource({
            "This is a bad text with bad words ( fuck )",
            "Another example with inappropriate content. motherfucker ",
            "I love ASSHOLE unit tests!"
    })
    void BadTextTest(String content) {
        assertTrue(moderationDictionary.containsCensorWord(content));
    }
}
