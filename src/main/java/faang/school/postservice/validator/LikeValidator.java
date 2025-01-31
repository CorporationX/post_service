package faang.school.postservice.validator;

import faang.school.postservice.exception.DataValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class LikeValidator {
    private final UserExistValidator userExistValidator;

    public void validateUser(Long userId) {
        if (userId == null) {
            throw new DataValidationException("UserId не может быть пустым");
        } else {
            userExistValidator.userExist(userId);
        }
    }
}
