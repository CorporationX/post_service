package faang.school.postservice.moderation;

import org.junit.jupiter.api.Test;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.FileWriter;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ModerationDictionaryTest {

    @Test
    void shouldLoadBadWordsFromFile() throws Exception {
        File tempFile = File.createTempFile("badwords", ".txt");
        try (FileWriter writer = new FileWriter(tempFile)) {
            writer.write("badword\nTestMat");
        }

        Resource mockResource = mock(Resource.class);
        when(mockResource.getFile()).thenReturn(tempFile);

        ModerationDictionaryComment dictionary = new ModerationDictionaryComment(mockResource);

        assertTrue(dictionary.containsBadWords("this is badword"));
        assertTrue(dictionary.containsBadWords("clean TESTMAT here"));
        assertFalse(dictionary.containsBadWords("everything is fine"));
        assertFalse(dictionary.containsBadWords(null));
        assertFalse(dictionary.containsBadWords(""));
    }
}
