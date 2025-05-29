package faang.school.postservice.config.context;

import faang.school.postservice.exception.UnauthorizedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class UserContext {

    private final ThreadLocal<Long> userIdHolder = new ThreadLocal<>();

    public void setUserId(long userId) {
        userIdHolder.set(userId);
    }

    public long getUserId() {
        log.debug("into UserContext.getUserId");
        Long userId = userIdHolder.get();
        log.debug("UserContext.getUserId userId: [{}]", userId);
        if (userId == null) {
            log.debug("throw new UnauthorizedException ...");
            throw new UnauthorizedException("User ID is missing. Please make sure 'x-user-id' " +
                    "header is included in the request.");
        }
        return userId;
    }

    public void clear() {
        userIdHolder.remove();
    }
}
