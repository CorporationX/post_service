package faang.school.postservice.service.post;

import faang.school.postservice.dto.languagetool.LanguageToolResponse;
import faang.school.postservice.model.Post;
import faang.school.postservice.service.languagetool.LanguageToolService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostCorrecterTest {
    @Mock
    private LanguageToolService languageToolService;

    @InjectMocks
    private PostCorrecter postCorrecter;

    private Post testPost;
    private List<Post> testListPosts;
    private static final String ORIGINAL_TEXT = "текст с ошипками.";
    private static final String CORRECTED_TEXT = "текст с ошибками.";

    @BeforeEach
    void setUp() {
        testPost = Post.builder()
                .id(1L)
                .content(ORIGINAL_TEXT)
                .build();
        testListPosts = List.of(testPost);
    }

    @Test
    void testCorrectingContentPostWhenCorrectReplacement() {
        List<LanguageToolResponse.Match> matches = new ArrayList<>();
        matches.add(new LanguageToolResponse.Match(
                "Орфографическая ошибка",
                8,
                8,
                List.of(new LanguageToolResponse.Replacement("ошибками"))
        ));
        LanguageToolResponse response = new LanguageToolResponse(matches);

        when(languageToolService.checkText(anyString()))
                .thenReturn(response);

        postCorrecter.correctingBatchPosts(testListPosts);

        Post correctedPost = testListPosts.get(0);
        assertEquals(CORRECTED_TEXT, correctedPost.getCorrectedContent());
        verify(languageToolService).checkText(ORIGINAL_TEXT);
    }

    @Test
    void correctingContentPostWhenEmptyMatches() {
        LanguageToolResponse response = new LanguageToolResponse(new ArrayList<>());

        when(languageToolService.checkText(anyString()))
                .thenReturn(response);

        postCorrecter.correctingBatchPosts(testListPosts);

        Post correctedPost = testListPosts.get(0);
        assertEquals(ORIGINAL_TEXT, correctedPost.getCorrectedContent());
        verify(languageToolService).checkText(ORIGINAL_TEXT);
    }
}