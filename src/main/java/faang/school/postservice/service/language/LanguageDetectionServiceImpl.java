package faang.school.postservice.service.language;

import com.github.pemistahl.lingua.api.Language;
import com.github.pemistahl.lingua.api.LanguageDetector;
import com.github.pemistahl.lingua.api.LanguageDetectorBuilder;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class LanguageDetectionServiceImpl implements LanguageDetectionService {

    private static final Map<Language, String> LANGUAGE_TO_TEXTGEARS_CODE = Map.ofEntries(
            Map.entry(Language.ENGLISH, "en-GB"),
            Map.entry(Language.RUSSIAN, "ru-RU"),
            Map.entry(Language.FRENCH, "fr-FR"),
            Map.entry(Language.GERMAN, "de-DE")
    );

    private final LanguageDetector detector;

    public LanguageDetectionServiceImpl() {
        this.detector = LanguageDetectorBuilder.fromLanguages(
                LANGUAGE_TO_TEXTGEARS_CODE.keySet().toArray(new Language[0])
        ).build();
    }

    @Override
    public String detectLanguageCode(String text) {
        Language detected = detector.detectLanguageOf(text);
        return LANGUAGE_TO_TEXTGEARS_CODE.getOrDefault(detected, "en-GB");
    }
}
