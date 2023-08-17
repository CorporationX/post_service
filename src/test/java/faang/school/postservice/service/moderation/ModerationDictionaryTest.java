package faang.school.postservice.service.moderation;

import faang.school.postservice.model.Comment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class ModerationDictionaryTest {

    @Mock
    Resource mockResource;
    @InjectMocks
    private ModerationDictionary moderationDictionary;

    static Stream<Arguments> argsProvider() {
        return Stream.of(
                Arguments.of("fuck", "fuck you idiot", false),
                Arguments.of("fuck", "Zhenya <3", true),
                Arguments.of("shit", "eat shit cocksucker", false)
        );
    }

    @BeforeEach
    public void setUp() {
        moderationDictionary = new ModerationDictionary();
        moderationDictionary.setProfanityWordsFile(mockResource);
    }

    @ParameterizedTest
    @MethodSource("argsProvider")
    public void testCheckCommentWithProfanityWord(String word, String content, boolean isVerified) throws IOException {
        InputStream inputStream = new ByteArrayInputStream(word.getBytes());
        Mockito.when(mockResource.getInputStream()).thenReturn(inputStream);

        moderationDictionary.initProfanityWords();

        Comment comment = Comment.builder().content(content).build();
        moderationDictionary.checkComment(comment);

        assertEquals(isVerified, comment.isVerified());
    }

    @Test
    void testCheckWordContent() {
        ReflectionTestUtils.setField(moderationDictionary, "obsceneWordsDictionary", getListOfObsceneWords());
        boolean actual1 = moderationDictionary.checkWordContent("you are stupid nigga");
        boolean actual2 = moderationDictionary.checkWordContent("this is good content");
        boolean actual3 = moderationDictionary.checkWordContent("A_N.`A#L in this post");
        boolean actual4 = moderationDictionary.checkWordContent("you can take p.o.r.n.o in this post");
        boolean actual5 = moderationDictionary.checkWordContent("here s_w_a_s_ti`k~a");

        assertEquals(true, actual1);
        assertEquals(false, actual2);
        assertEquals(true, actual3);
        assertEquals(true, actual4);
        assertEquals(true, actual5);
    }

    private Set<String> getListOfObsceneWords() {
        return Set.of("nigga", "anal", "porno", "swastika");
    }
}