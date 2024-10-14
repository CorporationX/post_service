package faang.school.postservice.validator.correcter;

import faang.school.postservice.dto.post.corrector.CorrectionResponseDto;
import faang.school.postservice.exception.correcter.TextGearsException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class PostCorrecterValidator {

    public void isCorrectResponse(CorrectionResponseDto response) {
        if (response == null || response.response() == null || !response.status()) {
            log.error("TextGears response is invalid");
            throw new TextGearsException("Response is invalid");
        }
    }
}
