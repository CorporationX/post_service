package faang.school.postservice.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

class ModerationDictionaryTest {

    private ModerationDictionary moderationDictionary;

    @BeforeEach
    void setUp() throws IOException {
        // Создаём временный JSON-файл с правильной структурой
        Path tempFile = Files.createTempFile("banned-words", ".json");
        tempFile.toFile().deleteOnExit();

        // Правильный JSON-формат с объектом-обёрткой
        String jsonContent = "{ \"bannedWords\": [\"badword1\", \"badword2\", \"offensiveword\"] }";
        Files.writeString(tempFile, jsonContent);

        moderationDictionary = new ModerationDictionary(tempFile.toString());
    }

    @Test
    void containsBannedWords_shouldReturnTrueWhenContentContainsBannedWord() {
        assertThat(moderationDictionary.containsBannedWords("This is a badword1 in the text.")).isTrue();
    }

    @Test
    void containsBannedWords_shouldReturnFalseWhenContentDoesNotContainBannedWord() {
        assertThat(moderationDictionary.containsBannedWords("This is a clean text without bad words.")).isFalse();
    }

    @Test
    void containsBannedWords_shouldHandleEmptyContentGracefully() {
        assertThat(moderationDictionary.containsBannedWords("")).isFalse();
    }

    @Test
    void containsBannedWords_shouldBeCaseInsensitive() {
        assertThat(moderationDictionary.containsBannedWords("This is a BADWORD1 in the text.")).isTrue();
    }

    @Test
    void containsBannedWords_shouldHandleSpecialCharacters() {
        assertThat(moderationDictionary.containsBannedWords("This is a badword1! in the text.")).isTrue();
    }
}