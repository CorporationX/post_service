package faang.school.postservice.config.context;

import org.springframework.stereotype.Component;

@Component
public class UserContext {

    private final ThreadLocal<Long> userIdHolder = new ThreadLocal<>();

    public void setUserId(long userId) {
        userIdHolder.set(userId);
    }

    public long getUserId() {
        Long id = userIdHolder.get();
        if (id == null) {
            throw new IllegalStateException("UserId is not set in UserContext"
                    + " (no x-user-id header / not in HTTP request)");
        }
        return id;
    }

    public Long getUserIdNullable() {
        return userIdHolder.get();
    }

    public void clear() {
        userIdHolder.remove();
    }
}
