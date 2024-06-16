package faang.school.postservice.validation.like;

import faang.school.postservice.dto.LikeDto;
import faang.school.postservice.exception.AlreadyExistsException;

@FunctionalInterface
public interface SimultaneousLikeValidator {
    void verifyNotExists(LikeDto dto);
    
    default void throwAlreadyExistsException(String message) {
        throw new AlreadyExistsException(message);
    }
}
