package faang.school.postservice.scheduledexecutor;

import faang.school.postservice.dto.post.ScheduledTaskDto;

public interface ScheduledTaskExecutor {

    void execute(ScheduledTaskDto dto);
}
