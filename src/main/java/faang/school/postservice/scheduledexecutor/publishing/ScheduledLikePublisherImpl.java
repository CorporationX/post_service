package faang.school.postservice.scheduledexecutor.publishing;

import faang.school.postservice.dto.post.ScheduledTaskDto;
import faang.school.postservice.scheduledexecutor.ScheduledTaskExecutor;
import faang.school.postservice.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ScheduledLikePublisherImpl implements ScheduledTaskExecutor {

    private final LikeService likeService;

    @Override
    public void execute(ScheduledTaskDto dto) {
        likeService.actWithLikeBySchedule(dto);
    }
}
