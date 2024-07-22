package faang.school.postservice.service.moderation;

import faang.school.postservice.config.moderation.ModerationDictionary;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class ModerationDictionaryTest {

    @InjectMocks
    private ModerationDictionary moderationDictionary;

    @BeforeEach
    void setUp() throws NoSuchFieldException, IllegalAccessException {

        moderationDictionary = new ModerationDictionary();
        Field field = ModerationDictionary.class.getDeclaredField("curseWordsPath");
        field.setAccessible(true);
        field.set(moderationDictionary, Path.of("src/main/resources/bad-words.txt"));

        moderationDictionary.init();
    }

    @Test
    void containsBadWord() {
        assertTrue(moderationDictionary.checkCurseWordsInPost("This is a damn test"));
        assertFalse(moderationDictionary.checkCurseWordsInPost("This is a clean comment"));
    }
}
