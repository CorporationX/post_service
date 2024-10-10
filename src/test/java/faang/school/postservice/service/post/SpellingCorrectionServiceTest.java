package faang.school.postservice.service.post;

import faang.school.postservice.client.external.spelling.TextGearsClient;
import faang.school.postservice.client.external.spelling.YandexSpellerClient;
import faang.school.postservice.dto.spelling_corrector.text_gears.TextGearsLang;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SpellingCorrectionServiceTest {
    @Mock
    private TextGearsClient textGearsClient;
    @Mock
    private YandexSpellerClient yandexSpellerClient;
    @InjectMocks
    private SpellingCorrectionService spellingCorrectionService;

    @Test
    void testCorrectedContentRuLang() {
        String content = "Some Content";
        String correctedContent = "Some Corrected Content";
        when(textGearsClient.detectLang(content)).thenReturn(TextGearsLang.RU);
        when(yandexSpellerClient.correctText(content)).thenReturn(correctedContent);

        String result = spellingCorrectionService.getCorrectedContent(content);

        assertEquals(result, correctedContent);

        verify(textGearsClient).detectLang(content);
        verify(textGearsClient, times(0)).correctText(content);
        verify(yandexSpellerClient).correctText(content);
    }

    @Test
    void testCorrectedContentOtherLang() {
        String content = "Some Content";
        String correctedContent = "Some Corrected Content";
        when(textGearsClient.detectLang(content)).thenReturn(TextGearsLang.OTHER);
        when(textGearsClient.correctText(content)).thenReturn(correctedContent);

        String result = spellingCorrectionService.getCorrectedContent(content);

        assertEquals(result, correctedContent);

        verify(textGearsClient).detectLang(content);
        verify(textGearsClient).correctText(content);
        verify(yandexSpellerClient, times(0)).correctText(content);
    }
}
