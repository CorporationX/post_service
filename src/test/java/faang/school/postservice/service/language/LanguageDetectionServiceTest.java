package faang.school.postservice.service.language;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LanguageDetectionServiceTest {

    private final LanguageDetectionService service = new LanguageDetectionServiceImpl();

    @Test
    @DisplayName("Should detect English language")
    void shouldDetectEnglish() {
        String result = service.detectLanguageCode("This is a test sentence.");
        assertEquals("en-GB", result);
    }

    @Test
    @DisplayName("Should detect Russian language")
    void shouldDetectRussian() {
        String result = service.detectLanguageCode("Это пример русского текста.");
        assertEquals("ru-RU", result);
    }

    @Test
    @DisplayName("Should detect French language")
    void shouldDetectFrench() {
        String result = service.detectLanguageCode("Ceci est une phrase en français.");
        assertEquals("fr-FR", result);
    }

    @Test
    @DisplayName("Should detect German language")
    void shouldDetectGerman() {
        String result = service.detectLanguageCode("Das ist ein deutscher Satz.");
        assertEquals("de-DE", result);
    }
}
