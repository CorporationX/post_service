package faang.school.postservice.api;

import faang.school.postservice.api.client.CorrectorClient;
import faang.school.postservice.model.Post;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostCorrectorTest {

    @Mock
    private CorrectorClient correctorClient;

    @InjectMocks
    private PostCorrector postCorrector;

    @Test
    @DisplayName("Correct post english content")
    void postCorrectorTest_correctPostContent() {
        Post post = Post.builder()
                .id(1L)
                .content("text")
                .build();
        String dialect = "en-US";
        String expected = "corrected text";
        when(correctorClient.getContentLanguageDialect(post.getContent())).thenReturn(dialect);
        when(correctorClient.getAutoCorrectedEnglishText(post.getContent(), dialect)).thenReturn(expected);

        String result = postCorrector.correctPost(post);

        verify(correctorClient).getContentLanguageDialect(post.getContent());
        verify(correctorClient).getAutoCorrectedEnglishText(post.getContent(), dialect);
        assertEquals(expected, result);
    }

    @Test
    @DisplayName("Correct post russian content")
    void postCorrectorTest_correctPostRussianContent() {
        Post post = Post.builder()
                .id(1L)
                .content("текст")
                .build();
        String dialect = "ru-RU";
        String expected = "corrected text";
        when(correctorClient.getContentLanguageDialect(post.getContent())).thenReturn(dialect);
        when(correctorClient.getCorrectedNonEnglishText(post.getContent(), dialect)).thenReturn(expected);

        String result = postCorrector.correctPost(post);

        assertEquals(expected, result);
        verify(correctorClient).getContentLanguageDialect(post.getContent());
        verify(correctorClient).getCorrectedNonEnglishText(post.getContent(), dialect);
    }
}
