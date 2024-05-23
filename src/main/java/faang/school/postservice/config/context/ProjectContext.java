package faang.school.postservice.config.context;

import org.springframework.stereotype.Component;

@Component
public class ProjectContext {
    private final ThreadLocal<Long> projectIdHolder = new ThreadLocal<>();

    public void setProjectId(long userId) {
        projectIdHolder.set(userId);
    }

    public long getProjectId() {
        return projectIdHolder.get();
    }

    public void clear() {
        projectIdHolder.remove();
    }
}