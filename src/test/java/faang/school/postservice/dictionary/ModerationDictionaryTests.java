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
public class ModerationDictionaryTests {

    @InjectMocks
    private ModerationDictionary moderationDictionary;

    @Mock
    private Resource dictionaryResource;

    @Test
    public void testInitLoadsSuccess() throws IOException {

        String fileContent = "badword1\nbadword2\n";
        InputStream inputStream = new java.io.ByteArrayInputStream(fileContent.getBytes());

        when(dictionaryResource.getInputStream()).thenReturn(inputStream);

        moderationDictionary.init();

        Set<String> badWords = moderationDictionary.getForbiddenWords();
        assertEquals(2, badWords.size());
        assertTrue(badWords.contains("badword1"));
        assertTrue(badWords.contains("badword2"));
    }

    @Test
    public void testInitThrowsIOException() throws IOException {

        when(dictionaryResource.getInputStream()).thenThrow(new IOException("File not found"));
        assertThrows(IOException.class, () -> moderationDictionary.init());
    }
}
