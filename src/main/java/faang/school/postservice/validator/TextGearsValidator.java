package faang.school.postservice.validator;

import faang.school.postservice.dto.text.gears.TextGearsResponse;

public interface TextGearsValidator {
    void isCorrectResponse(TextGearsResponse response);
}
