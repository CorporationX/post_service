package faang.school.postservice.config.context;

import faang.school.postservice.exception.DataValidationException;
import org.springframework.stereotype.Component;


@Component
public class UserContext {

    private final ThreadLocal<Long> userIdHolder = new ThreadLocal<>();

    public void setUserId(long userId) {
        userIdHolder.set(userId);
    }

    public long getUserId() {
        if (userIdHolder.get() == null) {
            throw new DataValidationException("x-user-id hedder is required for this request.");
        }
        return userIdHolder.get();
    }

    public void clear() {
        userIdHolder.remove();
    }
}
