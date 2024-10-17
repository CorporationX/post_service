package faang.school.postservice.api;

import faang.school.postservice.api.client.CorrectorClient;
import faang.school.postservice.dto.post.corrector.ApiResponse;
import faang.school.postservice.dto.post.corrector.AutoCorrectionResponse;
import faang.school.postservice.dto.post.corrector.CheckResponse;
import faang.school.postservice.dto.post.corrector.Error;
import faang.school.postservice.dto.post.corrector.LanguageResponse;
import faang.school.postservice.exception.CorrectorApiException;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostCorrectorTest {

    @Mock
    private CorrectorClient correctorClient;

    @InjectMocks
    private PostCorrector postCorrector;

    private Post post;

    @BeforeEach
    void setUp() {
        postCorrector.setDialects(Map.of(
                "ru", "ru-RU",
                "en", "en-US"));
        post = Post.builder()
                .content("text")
                .build();
    }

    @Test
    @DisplayName("Correct english post")
    void postCorrectorTest_correctEnglishPost() {
        String expectedContent = "corrected text";
        ApiResponse<LanguageResponse> languageResponse =
                initApiLanguageResponse(true, "en", 0, "");
        ApiResponse<AutoCorrectionResponse> autoCorrectionResponseApiResponse =
                initApiAutoCorrectionResponse(true, expectedContent, 0, "");

        when(correctorClient.getContentLanguageResponse(any())).thenReturn(languageResponse);
        when(correctorClient.getAutoCorrectedEnglishTextResponse(any(), any()))
                .thenReturn(autoCorrectionResponseApiResponse);

        postCorrector.correctPost(post);
        verify(correctorClient).getContentLanguageResponse(any());
        verify(correctorClient).getAutoCorrectedEnglishTextResponse(any(), any());
        assertEquals(expectedContent, post.getContent());
    }

    @Test
    @DisplayName("Correct russian post")
    void postCorrectorTest_correctRussianPost() {
        post.setContent("Я втарой");
        String expectedContent = "Я второй.";
        Error error = Error.builder()
                .offset(2)
                .length(6)
                .bad("втарой")
                .better(List.of("второй."))
                .build();
        ApiResponse<LanguageResponse> languageResponse =
                initApiLanguageResponse(true, "ru", 0, "");
        ApiResponse<CheckResponse> checkResponse =
                initApiCheckResponse(true, List.of(error), 0, "");

        when(correctorClient.getContentLanguageResponse(any())).thenReturn(languageResponse);
        when(correctorClient.getCheckResponseForNonEnglishText(any(), any())).thenReturn(checkResponse);

        postCorrector.correctPost(post);

        verify(correctorClient).getContentLanguageResponse(any());
        verify(correctorClient).getCheckResponseForNonEnglishText(any(), any());
        assertEquals(expectedContent, post.getContent());
    }

    @Test
    @DisplayName("Correct post with unknown language")
    void postCorrectorTest_correctPostWithUnknownLanguage() {
        ApiResponse<LanguageResponse> languageResponse =
                initApiLanguageResponse(true, "unknown", 0, "");
        when(correctorClient.getContentLanguageResponse(any())).thenReturn(languageResponse);

        assertThrows(CorrectorApiException.class, () -> postCorrector.correctPost(post));
        verify(correctorClient).getContentLanguageResponse(any());
    }

    @Test
    @DisplayName("Correct post with false language response")
    void postCorrectorTest_correctPostWithFalseLanguageResponse() {
        ApiResponse<LanguageResponse> languageResponse =
                initApiLanguageResponse(false, null, 1, "error");
        when(correctorClient.getContentLanguageResponse(any())).thenReturn(languageResponse);

        assertThrows(CorrectorApiException.class, () -> postCorrector.correctPost(post));
        verify(correctorClient).getContentLanguageResponse(any());
    }

    @Test
    @DisplayName("Correct english post with false auto correction response")
    void postCorrectorTest_correctEnglishPostWithFalseAutoCorrectionResponse() {
        ApiResponse<LanguageResponse> languageResponse =
                initApiLanguageResponse(true, "en", 0, "");
        ApiResponse<AutoCorrectionResponse> autoCorrectionResponseApiResponse =
                initApiAutoCorrectionResponse(false, null, 1, "error");

        when(correctorClient.getContentLanguageResponse(any())).thenReturn(languageResponse);
        when(correctorClient.getAutoCorrectedEnglishTextResponse(any(), any()))
                .thenReturn(autoCorrectionResponseApiResponse);

        assertThrows(CorrectorApiException.class, () -> postCorrector.correctPost(post));
        verify(correctorClient).getContentLanguageResponse(any());
        verify(correctorClient).getAutoCorrectedEnglishTextResponse(any(), any());
    }

    @Test
    @DisplayName("Correct non-english post with false check response")
    void postCorrectorTest_correctNonEnglishPostWithFalseCheckResponse() {
        ApiResponse<LanguageResponse> languageResponse =
                initApiLanguageResponse(true, "ru", 0, "");
        ApiResponse<CheckResponse> checkResponse =
                initApiCheckResponse(false, null, 1, "error");

        when(correctorClient.getContentLanguageResponse(any())).thenReturn(languageResponse);
        when(correctorClient.getCheckResponseForNonEnglishText(any(), any())).thenReturn(checkResponse);

        assertThrows(CorrectorApiException.class, () -> postCorrector.correctPost(post));
        verify(correctorClient).getContentLanguageResponse(any());
        verify(correctorClient).getCheckResponseForNonEnglishText(any(), any());
    }


    private ApiResponse<LanguageResponse> initApiLanguageResponse(
            boolean status, String language, int errorCode, String description) {
        return new ApiResponse<>(status, new LanguageResponse(language), errorCode, description);
    }

    private ApiResponse<AutoCorrectionResponse> initApiAutoCorrectionResponse(
            boolean status, String correctedText, int errorCode, String description) {
        return new ApiResponse<>(status, new AutoCorrectionResponse(correctedText), errorCode, description);
    }

    private ApiResponse<CheckResponse> initApiCheckResponse(
            boolean status, List<Error> errors, int errorCode, String description) {
        return new ApiResponse<>(status, new CheckResponse(errors), errorCode, description);
    }
}
