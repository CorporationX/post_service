package faang.school.postservice.dictionary;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ModerationDictionaryTest {

    @Mock
    private Resource dictionaryFile;

    @InjectMocks
    private ModerationDictionary moderationDictionary;

    @Test
    public void testInit_Success() throws IOException {
        String fileContent = "badword1\nbadword2\n";
        InputStream inputStream = new java.io.ByteArrayInputStream(fileContent.getBytes());

        when(dictionaryFile.getInputStream()).thenReturn(inputStream);

        moderationDictionary.init();

        Set<String> badWords = moderationDictionary.getBadWords();
        assertEquals(2, badWords.size());
        assertTrue(badWords.contains("badword1"));
        assertTrue(badWords.contains("badword2"));
    }

    @Test
    public void testInit_EmptyFile() throws IOException {
        String fileContent = "";
        InputStream inputStream = new java.io.ByteArrayInputStream(fileContent.getBytes());

        when(dictionaryFile.getInputStream()).thenReturn(inputStream);

        moderationDictionary.init();

        Set<String> badWords = moderationDictionary.getBadWords();
        assertTrue(badWords.isEmpty());
    }

    @Test
    public void testInit_IOException() throws IOException {
        when(dictionaryFile.getInputStream()).thenThrow(new IOException("File not found"));

        assertThrows(IOException.class, () -> {
            moderationDictionary.init();
        });
    }
}
