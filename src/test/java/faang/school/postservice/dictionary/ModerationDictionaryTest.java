package faang.school.postservice.dictionary;

import faang.school.postservice.service.comment.CommentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ModerationDictionaryTest {
    @Autowired
    private ModerationDictionary moderationDictionary;
    @MockBean
    private CommentService commentService;

    @Test
    void containsUnwantedWords() {
        assertTrue(moderationDictionary.containsUnwantedWords("This comment contains nigger"));
        assertFalse(moderationDictionary.containsUnwantedWords("This comment is clean"));
    }
}