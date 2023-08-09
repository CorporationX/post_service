package faang.school.postservice.scheduledexecutor.publishing;

import faang.school.postservice.dto.post.ScheduledTaskDto;
import faang.school.postservice.scheduledexecutor.ScheduledTaskExecutor;
import faang.school.postservice.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ScheduledPostPublisherImpl implements ScheduledTaskExecutor {

    private final PostService postService;

    @Override
    public void execute(ScheduledTaskDto dto) {
        postService.actWithPostBySchedule(dto);
    }
}
