package faang.school.postservice.config.context;

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
        Long userId = userIdHolder.get();
        if (userId == null) {
            log.warn("userId is null");
        }
        return userId;
    }

    public void clear() {
        userIdHolder.remove();
    }
}
