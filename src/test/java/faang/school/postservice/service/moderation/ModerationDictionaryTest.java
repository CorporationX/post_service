package faang.school.postservice.service.moderation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestPropertySource(properties = {"moderation.badWords=bad,words"})
public class ModerationDictionaryTest {
    private ModerationDictionary moderationDictionary;

    @BeforeEach
    public void setUp() {
        String[] badWords = List.of("bad", "words").toArray(new String[0]);
        moderationDictionary = new ModerationDictionary(badWords);
    }

    @Test
    public void testWhenCheckBadWords_thenCorrect() {
        assertTrue(moderationDictionary.containsBadWords("this is bad"));
        assertTrue(moderationDictionary.containsBadWords("these are words"));
        assertFalse(moderationDictionary.containsBadWords("this is good"));
    }
}