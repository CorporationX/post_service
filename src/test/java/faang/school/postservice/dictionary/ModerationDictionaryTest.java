package faang.school.postservice.dictionary;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;


import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

//@SpringBootTest
//public class ModerationDictionaryTest {
//    @Autowired
//    private ModerationDictionary moderationDictionary;
//
//    @BeforeEach
//    void setUp() {}
//
//    @ParameterizedTest
//    @CsvSource({
//            "This is a good text without any bad words.",
//            "Another example without any inappropriate content.",
//            "I love unit tests!"
//    })
//    void GoodTextTest(String content) {
//        assertFalse(moderationDictionary.containsCensorWord(content));
//    }
//
//    @ParameterizedTest
//    @CsvSource({
//            "This is a bad text with bad words ( fuck )",
//            "Another example with inappropriate content. motherfucker ",
//            "I love ASSHOLE unit tests!"
//    })
//    void BadTextTest(String content) {
//        assertTrue(moderationDictionary.containsCensorWord(content));
//    }
//}
