package faang.school.postservice.service.post;

import faang.school.postservice.dto.languagetool.LanguageToolResponse;
import faang.school.postservice.model.Post;
import faang.school.postservice.service.PostService;
import faang.school.postservice.service.languagetool.LanguageToolService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostCorrecterTest {

    @Mock
    private PostService postService;

    @Mock
    private LanguageToolService languageToolService;

    @InjectMocks
    private PostCorrecter postCorrecter;

    private Post testPost;
    private final String originalContent = "текст с ошипками.";

    @BeforeEach
    void setUp() {
        testPost = Post.builder()
                .id(1L)
                .content(originalContent)
                .build();
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
                .thenReturn(CompletableFuture.completedFuture(response));

        CompletableFuture<Void> result = postCorrecter.correctingContentPost(testPost);

        assertNull(result.join());
        verify(postService).updateCorrectedContentOfPost(
                eq(testPost),
                eq("текст с ошибками.")
        );
    }

    @Test
    void correctingContentPostWhenEmptyMatches() {
        LanguageToolResponse response = new LanguageToolResponse(new ArrayList<>());

        when(languageToolService.checkText(anyString()))
                .thenReturn(CompletableFuture.completedFuture(response));

        CompletableFuture<Void> result = postCorrecter.correctingContentPost(testPost);

        assertNull(result.join());
        verify(postService).updateCorrectedContentOfPost(eq(testPost), eq(originalContent));
    }
}