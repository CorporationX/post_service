package faang.school.postservice.service;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.validator.AlbumValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserContext userContext;
    private final AlbumValidator validator;

    public long getUserId() {
        long userId = userContext.getUserId();
        validator.validateUser(userId);
        return userId;
    }
}
