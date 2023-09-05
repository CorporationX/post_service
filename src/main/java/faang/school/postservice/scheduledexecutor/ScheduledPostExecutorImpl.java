package faang.school.postservice.scheduledexecutor;

import faang.school.postservice.dto.post.ScheduledTaskDto;
import faang.school.postservice.service.ScheduledPostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ScheduledPostExecutorImpl implements ScheduledTaskExecutor {

    private final ScheduledPostService scheduledPostService;

    @Override
    public ScheduledTaskDto actWithScheduledTask(ScheduledTaskDto dto) {
        return scheduledPostService.savePostBySchedule(dto);
    }
}
