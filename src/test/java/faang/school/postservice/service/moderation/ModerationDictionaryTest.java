package faang.school.postservice.service.moderation;

import faang.school.postservice.model.Comment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class ModerationDictionaryTest {
    ModerationDictionary moderationDictionary;

    @Mock
    Resource mockResource;

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

        moderationDictionary.initialize();

        Comment comment = Comment.builder().content(content).build();
        moderationDictionary.checkComment(comment);

        assertEquals(isVerified, comment.isVerified());
    }
}