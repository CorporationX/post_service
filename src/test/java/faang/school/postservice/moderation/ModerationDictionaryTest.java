package faang.school.postservice.moderation;

import faang.school.postservice.model.Post;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ModerationDictionaryTest {

    @InjectMocks
    private ModerationDictionary moderationDictionary;
    @Mock
    private Dictionary dictionary;

    private static final String SWEAR_WORD = "bug";
    private static final long ID_ONE = 1L;
    private static final long ID_TWO = 2L;
    private static final String CONTENT = "content";
    private static final String SWEAR_CONTENT = "bug";

    private Map<Long, String> unverifiedContent;

    @BeforeEach
    public void init() {
        unverifiedContent = Map.of(ID_ONE, CONTENT, ID_TWO, SWEAR_CONTENT);
        dictionary = new Dictionary(Set.of(SWEAR_WORD));
        moderationDictionary = new ModerationDictionary(dictionary);
    }

    @Test
    @DisplayName("Успешная верификация контента")
    public void whenSearchSwearWordsThenVerifiedSuccess() {
        Map<Long, Boolean> result = moderationDictionary.searchSwearWords(unverifiedContent);

        assertEquals(2, result.size());
        assertTrue(result.get(ID_ONE));
        assertFalse(result.get(ID_TWO));
    }
}