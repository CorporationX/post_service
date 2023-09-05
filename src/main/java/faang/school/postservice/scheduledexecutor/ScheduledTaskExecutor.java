package faang.school.postservice.scheduledexecutor;

import faang.school.postservice.dto.post.ScheduledTaskDto;

public interface ScheduledTaskExecutor {

    ScheduledTaskDto actWithScheduledTask(ScheduledTaskDto dto);
}
