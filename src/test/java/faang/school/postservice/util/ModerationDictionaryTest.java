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
        Path tempFile = Files.createTempFile("banned-words", ".json");
        tempFile.toFile().deleteOnExit();

        String jsonContent = "[\"badword1\", \"badword2\", \"offensiveword\"]";
        Files.writeString(tempFile, jsonContent);

        moderationDictionary = new ModerationDictionary(tempFile.toString());
    }

    @Test
    void containsBannedWords_shouldReturnTrueWhenContentContainsBannedWord() {
        boolean result = moderationDictionary.containsBannedWords("This is a badword1 in the text.");
        assertThat(result).isTrue();
    }

    @Test
    void containsBannedWords_shouldReturnFalseWhenContentDoesNotContainBannedWord() {
        boolean result = moderationDictionary.containsBannedWords("This is a clean text without bad words.");
        assertThat(result).isFalse();
    }

    @Test
    void containsBannedWords_shouldHandleEmptyContentGracefully() {
        boolean result = moderationDictionary.containsBannedWords("");
        assertThat(result).isFalse();
    }

    @Test
    void containsBannedWords_shouldBeCaseInsensitive() {
        boolean result = moderationDictionary.containsBannedWords("This is a BADWORD1 in the text.");
        assertThat(result).isTrue();
    }

    @Test
    void containsBannedWords_shouldHandleSpecialCharacters() {
        boolean result = moderationDictionary.containsBannedWords("This is a badword1! in the text.");
        assertThat(result).isTrue();
    }
}