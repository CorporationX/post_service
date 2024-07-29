package faang.school.postservice.validator;

import org.springframework.stereotype.Component;

@Component
public class ControllerValidator {
    private static final String MESSAGE_DTO_IS_NULL = "Dto is null";

    public void validateId(Long id, String message) {
        if (id < 0) {
            throw new RuntimeException(message);
        }
    }

    public void validateDto(Object dto) {
        if (dto == null) {
            throw new RuntimeException(MESSAGE_DTO_IS_NULL);
        }
    }
}
