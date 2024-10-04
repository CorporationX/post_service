package faang.school.postservice.moderation;

import faang.school.postservice.model.Post;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ModerationDictionaryTest {

    @InjectMocks
    private ModerationDictionary moderationDictionary;

    private List<String> dictionary;
    private static final String WORD = "слово";
    private static final String BAD_WORD = "баг";

    private List<Post> unverifiedPosts;
    private Post first;
    private Post second;


    @BeforeEach
    public void init() {
        first = Post.builder()
                .content(WORD)
                .build();
        second = Post.builder()
                .content(BAD_WORD)
                .build();

        unverifiedPosts = Arrays.asList(first, second);
        dictionary = List.of(BAD_WORD);
        moderationDictionary = new ModerationDictionary(dictionary);
    }

    @Test
    @DisplayName("Успешная верификация постов")
    public void whenSwearWordThenVerifiedSuccess() {
        List<Post> result = moderationDictionary.searchSwearWords(unverifiedPosts);

        assertEquals(1, dictionary.size());
        assertEquals(BAD_WORD, dictionary.get(0));
        assertEquals(2, result.size());
        assertTrue(result.get(0).getVerified());
        assertFalse(result.get(1).getVerified());
    }
}