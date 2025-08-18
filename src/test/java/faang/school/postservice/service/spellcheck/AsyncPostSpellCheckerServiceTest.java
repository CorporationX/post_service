package faang.school.postservice.service.spellcheck;

import faang.school.postservice.client.SpellCheckClient;
import faang.school.postservice.dto.spellcheck.SpellCheckResponse;
import faang.school.postservice.dto.spellcheck.SpellCheckResultDto;
import faang.school.postservice.model.Post;
import faang.school.postservice.service.language.LanguageDetectionService;
import faang.school.postservice.service.post.PostService;
import faang.school.postservice.validation.spellcheck.PostSpellCheckValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AsyncPostSpellCheckerServiceTest {

    @Mock
    private PostService postService;

    @Mock
    private PostSpellCheckValidator validator;

    @Mock
    private LanguageDetectionService languageService;

    @Mock
    private SpellCheckClient spellCheckClient;

    @InjectMocks
    private AsyncPostSpellCheckerService service;

    private final Post post = new Post();

    @BeforeEach
    void setup() {
        post.setId(1L);
        post.setContent("Teh contnt with errrs");
    }

    @Test
    @DisplayName("Should skip post if text is invalid")
    void shouldSkipInvalidPost() {
        when(validator.hasValidText(post)).thenReturn(false);

        service.correctPostBatchAsync(List.of(post));

        verifyNoInteractions(languageService);
        verifyNoInteractions(spellCheckClient);
        verify(postService, never()).updatePostContent(any(), any());
    }

    @Test
    @DisplayName("Should detect language and correct content")
    void shouldCorrectValidPost() {
        when(validator.hasValidText(post)).thenReturn(true);
        when(languageService.detectLanguageCode(post.getContent())).thenReturn("en");

        SpellCheckResultDto resultDto = new SpellCheckResultDto("The content with errors.");
        SpellCheckResponse response = new SpellCheckResponse(resultDto, true);
        when(spellCheckClient.checkText(post.getContent(), "en")).thenReturn(response);

        service.correctPostBatchAsync(List.of(post));

        verify(postService).updatePostContent(post, "The content with errors.");
    }
}
