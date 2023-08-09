package faang.school.postservice.scheduledexecutor.deleting;

import faang.school.postservice.dto.post.ScheduledTaskDto;
import faang.school.postservice.scheduledexecutor.ScheduledTaskExecutor;
import faang.school.postservice.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ScheduledLikeDeleterImpl implements ScheduledTaskExecutor {

    private final LikeService likeService;

    @Override
    public void execute(ScheduledTaskDto dto) {
        likeService.addToDeleteLikeBySchedule(dto);
    }
}
