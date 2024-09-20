package faang.school.postservice.config.context;

import org.springframework.stereotype.Component;

@Component
public class UserContext {

    private final ThreadLocal<Long> userIdHolder = new ThreadLocal<>();

    public void setUserId(long userId) {
        userIdHolder.set(userId);
    }

    public long getUserId() {
        if (userIdHolder.get() == null) {
            userIdHolder.set(0L);
        }
        return userIdHolder.get();
    }

    public void clear() {
        userIdHolder.remove();
    }
}
