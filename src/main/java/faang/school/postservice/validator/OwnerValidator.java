package faang.school.postservice.validator;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.exception.MoreOneOwnerException;
import faang.school.postservice.exception.NotResourceOwnerException;
import faang.school.postservice.exception.OwnerIdNotPresentException;
import faang.school.postservice.model.ErrorType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OwnerValidator {
    private final UserContext userContext;

    public void validateOwnerIds(Long userId, Long projectId) {
        log.debug("Start owner ids validation with userId={}, projectIds={}", userId, projectId);
        if (userId == null && projectId == null) {
            throw new OwnerIdNotPresentException(ErrorType.OWNER_ID_NOT_PRESENT);
        }

        if (userId != null && projectId != null) {
            throw new MoreOneOwnerException(ErrorType.MORE_ONE_OWNER);
        }

        validateOwner(userId);
        validateOwner(projectId);
    }

    public void validateOwner(Long ownerId) {
        log.debug("Start owner validation with id={}", ownerId);
        long currentId = userContext.getUserId();
        if (ownerId != null && currentId != ownerId) {
            throw new NotResourceOwnerException("Id={} is not owner account with id={}", currentId, ownerId);
        }
    }
}
