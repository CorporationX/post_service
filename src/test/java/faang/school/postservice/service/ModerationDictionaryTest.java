package faang.school.postservice.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = PostModerationDictionaryImpl.class)
public class ModerationDictionaryTest {

    @Autowired
    private PostModerationDictionaryImpl moderationDictionary;

    @Test
    public void testIsTextWithoutForbiddenWords() {
        assertTrue(moderationDictionary.isTextWithoutForbiddenWords("Добрый текст"));
        assertFalse(moderationDictionary.isTextWithoutForbiddenWords("Шмара текст"));
    }
}
