package faang.school.postservice.config.context;

import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicLong;

@Component
public class UserContext {

    private final AtomicLong userIdHolder = new AtomicLong();

    public void setUserId(long userId) {
        userIdHolder.set(userId);
    }

    public long getUserId() {
        return userIdHolder.get();
    }

    public void clear() {
        userIdHolder.set(Long.MIN_VALUE);
    }
}
