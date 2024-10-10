package faang.school.postservice.api.client;

import faang.school.postservice.config.corrector.CorrectorClientConfig;
import faang.school.postservice.config.corrector.CorrectorClientParams;
import faang.school.postservice.exception.CorrectorApiException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CorrectorClientTest {
    private CorrectorClient correctorClient;
    private final Map<String, String> dialects = Map.ofEntries(
            Map.entry("en", "en-US"),
            Map.entry("ru", "ru-RU"));

    @BeforeEach
    void setUp() {
        CorrectorClientParams params = new CorrectorClientParams();
        params.setUrl("https://api.textgears.com");
        params.setApiKey("9AAv9PgZqSaD5jXp");
        CorrectorClientConfig config = new CorrectorClientConfig(params);
        WebClient webClient = config.correctorWebClient();
        correctorClient = new CorrectorClient(webClient);
        correctorClient.setDialects(dialects);
    }

    @Test
    @DisplayName("Get dialect of english content")
    void correctorClientTest_getDialectOfEnglishContent() {
        String content = "My name is  test";
        String expected = "en-US";

        String result = correctorClient.getContentLanguageDialect(content);

        assertEquals(expected, result);
    }

    @Test
    @DisplayName("Get dialect of russian content")
    void correctorClientTest_getDialectOfRussianContent() {
        String content = "Мое имя тест";
        String expected = "ru-RU";

        String result = correctorClient.getContentLanguageDialect(content);

        assertEquals(expected, result);
    }

    @Test
    @DisplayName("Get  dialect of unknown language")
    void correctorClientTest_getDialectOfUnknownLanguage() {
        String content = "My name test";
        correctorClient.setDialects(Map.of("ru", "ru-RU"));

        assertThrows(CorrectorApiException.class, () -> correctorClient.getContentLanguageDialect(content));
    }

    @Test
    @DisplayName("Autro correct english text")
    void correctorClientTest_autoCorrectEnglishText() {
        String content = "My name are test";
        String dialect = "en-US";
        String expected = "My name is Test";

        String result = correctorClient.getAutoCorrectedEnglishText(content, dialect);

        assertEquals(expected, result);
    }

    @Test
    @DisplayName("Correct non english text")
    void correctorClientTest_correctNonEnglishText() {
        String content = "Я втарой";
        String dialect = "ru-RU";
        String expected = "Я второй.";

        String result = correctorClient.getCorrectedNonEnglishText(content, dialect);

        assertEquals(expected, result);
    }
}
