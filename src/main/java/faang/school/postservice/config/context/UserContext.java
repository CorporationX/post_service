package faang.school.postservice.config.context;

import org.springframework.stereotype.Component;

@Component
public class UserContext {

    private final ThreadLocal<Long> userIdHolder = new ThreadLocal<>();

    public void setUserId(long userId) {
        userIdHolder.set(userId);
    }

    public long getUserId() {
        //to do
        //return userIdHolder.get();
        return 10L;
    }

    public void clear() {
        userIdHolder.remove();
    }
}
