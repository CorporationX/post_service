package faang.school.postservice.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PostCorrectionValidator {

    public boolean isTextValid(String text) {
        return text != null && !text.isBlank();
    }

    public boolean isCorrectionValid(String correctedText) {
        return correctedText != null && !correctedText.isBlank();
    }

    public boolean isCorrectionDifferent(String originalText, String correctedText) {
        return !originalText.equals(correctedText);
    }
}