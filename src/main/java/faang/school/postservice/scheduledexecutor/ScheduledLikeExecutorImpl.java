package faang.school.postservice.scheduledexecutor;

import faang.school.postservice.dto.post.ScheduledTaskDto;
import faang.school.postservice.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ScheduledLikeExecutorImpl implements ScheduledTaskExecutor {

    private final LikeService likeService;

    @Override
    public ScheduledTaskDto saveTaskBySchedule(ScheduledTaskDto dto) {
        return likeService.actWithLikeBySchedule(dto);
    }
}
