package faang.school.postservice.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

class ModerationDictionaryValidationUtilTest {

    private ModerationDictionaryUtil moderationDictionaryUtil;

    @BeforeEach
    void setUp() throws IOException {
        // Создаём временный JSON-файл с правильной структурой
        Path tempFile = Files.createTempFile("banned-words", ".json");
        tempFile.toFile().deleteOnExit();

        // Правильный JSON-формат с объектом-обёрткой
        String jsonContent = "{ \"bannedWords\": [\"badword1\", \"badword2\", \"offensiveword\"] }";
        Files.writeString(tempFile, jsonContent);

        moderationDictionaryUtil = new ModerationDictionaryUtil(tempFile.toString());
    }

    @Test
    void containsBannedWords_shouldReturnTrueWhenContentContainsBannedWord() {
        assertThat(moderationDictionaryUtil.containsBannedWords("This is a badword1 in the text.")).isTrue();
    }

    @Test
    void containsBannedWords_shouldReturnFalseWhenContentDoesNotContainBannedWord() {
        assertThat(moderationDictionaryUtil.containsBannedWords("This is a clean text without bad words.")).isFalse();
    }

    @Test
    void containsBannedWords_shouldHandleEmptyContentGracefully() {
        assertThat(moderationDictionaryUtil.containsBannedWords("")).isFalse();
    }

    @Test
    void containsBannedWords_shouldBeCaseInsensitive() {
        assertThat(moderationDictionaryUtil.containsBannedWords("This is a BADWORD1 in the text.")).isTrue();
    }

    @Test
    void containsBannedWords_shouldHandleSpecialCharacters() {
        assertThat(moderationDictionaryUtil.containsBannedWords("This is a badword1! in the text.")).isTrue();
    }
}