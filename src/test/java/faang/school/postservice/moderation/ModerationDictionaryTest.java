package faang.school.postservice.moderation;

import faang.school.postservice.model.Post;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ModerationDictionaryTest {

    @InjectMocks
    private ModerationDictionary moderationDictionary;
    @Mock
    private Dictionary dictionary;

    private static final String WORD = "слово";
    private static final String BAD_WORD = "баг";

    private List<Verifyible> verifyibles;
    private Post first;
    private Post second;

    @BeforeEach
    public void init() {
        ReflectionTestUtils.setField(dictionary, "dictionary", Set.of(BAD_WORD));

        first = Post.builder()
                .content(WORD)
                .build();
        second = Post.builder()
                .content(BAD_WORD)
                .build();

        verifyibles = List.of(first, second);
    }

    @Test
    @DisplayName("Успешная верификация контента")
    public void whenSearchSwearWordsThenVerifiedSuccess() {
        moderationDictionary.searchSwearWords(verifyibles);

        verify(dictionary, times(2)).getDictionary();
    }
}