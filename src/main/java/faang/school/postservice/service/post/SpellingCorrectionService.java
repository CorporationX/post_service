package faang.school.postservice.service.post;

import faang.school.postservice.dto.spelling_corrector.text_gears.TextGearsLang;
import faang.school.postservice.client.external.spelling.TextGearsClient;
import faang.school.postservice.client.external.spelling.YandexSpellerClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SpellingCorrectionService {
    private final TextGearsClient textGearsClient;
    private final YandexSpellerClient yandexSpellerClient;

    public String getCorrectedContent(String content) {
        TextGearsLang lang = textGearsClient.detectLang(content);

        if (lang.equals(TextGearsLang.RU)) {
            return yandexSpellerClient.correctText(content);
        }

        return textGearsClient.correctText(content);
    }
}
