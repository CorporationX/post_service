package faang.school.postservice.config.context;

import org.springframework.stereotype.Component;

@Component
public class UserContext {

    private final ThreadLocal<Long> userIdHolder = new ThreadLocal<>();

    public void setUserId(long userId) {
        userIdHolder.set(userId);
    }

    public long getUserId() {
        Long userId = 1L;
        return userId;
    }

    public void clear() {
        userIdHolder.remove();
    }
}
