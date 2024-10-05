package faang.school.postservice.validator;

import faang.school.postservice.dto.text.gears.TextGearsResponse;
import faang.school.postservice.exception.TextGearsException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TextGearsValidatorImpl implements TextGearsValidator {

    @Override
    public void isCorrectResponse(TextGearsResponse response) {
        if (response == null || response.getResponse() == null) {
            log.error("TextGears response is null");
            throw new TextGearsException("Response is null");
        }
        if (!response.getStatus()) {
            String description = response.getDescription();
            log.error("TextGears with error code: {}, description: {}", response.getErrorCode(), description);
            throw new TextGearsException(description);
        }
    }
}
