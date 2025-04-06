package faang.school.postservice.config.context;

import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UserContext {

    private final ThreadLocal<Long> userIdHolder = new ThreadLocal<>();

    public void setUserId(long userId) {
        userIdHolder.set(userId);
    }

    public Optional<Long> getUserIdOptional() {
        return Optional.ofNullable(userIdHolder.get());
    }

    public long getUserId() {
        return userIdHolder.get();
    }

    public void clear() {
        userIdHolder.remove();
    }
}
