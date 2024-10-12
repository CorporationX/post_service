package faang.school.postservice.api.client;

import faang.school.postservice.config.corrector.CorrectorClientConfig;
import faang.school.postservice.config.corrector.CorrectorClientParams;
import faang.school.postservice.dto.post.corrector.ApiResponse;
import faang.school.postservice.dto.post.corrector.AutoCorrectionResponse;
import faang.school.postservice.dto.post.corrector.CheckResponse;
import faang.school.postservice.dto.post.corrector.Error;
import faang.school.postservice.dto.post.corrector.LanguageResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CorrectorClientTest {
    private CorrectorClient correctorClient;

    @BeforeEach
    void setUp() {
        CorrectorClientParams params = new CorrectorClientParams();
        params.setUrl("https://api.textgears.com");
        params.setApiKey("9AAv9PgZqSaD5jXp");
        CorrectorClientConfig config = new CorrectorClientConfig(params);
        WebClient webClient = config.correctorWebClient();
        correctorClient = new CorrectorClient(webClient);
    }

    @Test
    @DisplayName("Get language of english content")
    void correctorClientTest_getLanguageOfEnglishContent() {
        String content = "My name is test";
        String expectedLanguage = "en";

        ApiResponse<LanguageResponse> result = correctorClient.getContentLanguageResponse(content);
        String resultLanguage = result.response().language();

        assertTrue(result.status());
        assertEquals(expectedLanguage, resultLanguage);
    }

    @Test
    @DisplayName("Get dialect of russian content")
    void correctorClientTest_getDialectOfRussianContent() {
        String content = "Мое имя тест";
        String expectedLanguage = "ru";

        ApiResponse<LanguageResponse> result = correctorClient.getContentLanguageResponse(content);
        String resultLanguage = result.response().language();

        assertTrue(result.status());
        assertEquals(expectedLanguage, resultLanguage);
    }

    @Test
    @DisplayName("Auto correct english text")
    void correctorClientTest_autoCorrectEnglishText() {
        String content = "My name are test";
        String dialect = "en-US";
        String expected = "My name is Test";

        ApiResponse<AutoCorrectionResponse> result =
                correctorClient.getAutoCorrectedEnglishTextResponse(content, dialect);
        String correctedResult = result.response().corrected();

        assertTrue(result.status());
        assertEquals(expected, correctedResult);
    }

    @Test
    @DisplayName("Get checking response for non english text")
    void correctorClientTest_getCheckingResponseForNonEnglishText() {
        String content = "Я втарой";
        String dialect = "ru-RU";
        Error expected = Error.builder()
                .offset(2)
                .length(6)
                .bad("втарой")
                .better(List.of("второй."))
                .type("grammar")
                .build();

        ApiResponse<CheckResponse> result = correctorClient.getCheckResponseForNonEnglishText(content, dialect);
        List<Error> errors = result.response().errors();

        assertTrue(result.status());
        assertEquals(1, errors.size());
        assertEquals(expected, errors.get(0));
    }
}
