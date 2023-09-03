package faang.school.postservice.scheduledexecutor;

import faang.school.postservice.dto.post.ScheduledTaskDto;
import faang.school.postservice.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ScheduledPostExecutorImpl implements ScheduledTaskExecutor {

    private final ScheduledPostService scheduledPostService;

    @Override
    public ScheduledTaskDto saveScheduledTask(ScheduledTaskDto dto) {
        return scheduledPostService.savePostBySchedule(dto);
    }
}
