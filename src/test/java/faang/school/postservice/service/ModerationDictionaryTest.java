package faang.school.postservice.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
@SpringBootTest(classes = ModerationDictionaryImpl.class)
@TestPropertySource(locations = "classpath:application-test.yaml")
public class ModerationDictionaryTest {
    @Autowired
    private ModerationDictionaryImpl moderationDictionary;

    @Test
    public void isTextAreCorrectTest() {
        String correctText = "This is a text";
        String incorrectText = "This is a test";

        assertTrue(moderationDictionary.isTextAreCorrect(correctText));
        assertFalse(moderationDictionary.isTextAreCorrect(incorrectText));
    }
}
