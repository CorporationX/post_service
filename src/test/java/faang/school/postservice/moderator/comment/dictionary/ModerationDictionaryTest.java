package faang.school.postservice.moderator.comment.dictionary;

import faang.school.postservice.moderator.dictionary.ModerationDictionary;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class ModerationDictionaryTest {
    @InjectMocks
    private ModerationDictionary moderationDictionary;

    @Test
    public void testReadBadWords(){
        Set<String> correctSet = new HashSet<>(Set.of("badWord1", "badWord2", "badWord3"));
        moderationDictionary.setPathToFile("src/test/java/faang/school/postservice/moderator/comment/badWords/badWords");

        moderationDictionary.readBadWords();

        assertEquals(correctSet, moderationDictionary.getBadWords());
    }

    @Test
    public void testIncorrectPathToFile() {
        moderationDictionary.setPathToFile("");

        assertThrows(RuntimeException.class, () -> moderationDictionary.readBadWords());
    }

    @Test
    public void testWhenStringContainsBadWord() {
        String inputString = ",badWord!";
        moderationDictionary.setBadWords(Set.of("badWord"));

        boolean result = moderationDictionary.isContainsBadWordInTheText(inputString);

        assertTrue(result);
    }

    @Test
    public void testWhenStringNotContainsBadWord() {
        String inputString = "badWord";

        boolean result = moderationDictionary.isContainsBadWordInTheText(inputString);

        assertFalse(result);
    }
}