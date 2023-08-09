package faang.school.postservice.scheduledexecutor.deleting;

import faang.school.postservice.dto.post.ScheduledTaskDto;
import faang.school.postservice.scheduledexecutor.ScheduledTaskExecutor;
import faang.school.postservice.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ScheduledPostDeleterImpl implements ScheduledTaskExecutor {

    private final PostService postService;

    @Override
    public void execute(ScheduledTaskDto dto) {
        postService.addToDeletePostBySchedule(dto);
    }
}
