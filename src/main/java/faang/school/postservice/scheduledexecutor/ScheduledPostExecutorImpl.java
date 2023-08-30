package faang.school.postservice.scheduledexecutor;

import faang.school.postservice.dto.post.ScheduledTaskDto;
import faang.school.postservice.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ScheduledPostExecutorImpl implements ScheduledTaskExecutor {

    private final PostService postService;

    @Override
    public ScheduledTaskDto actWithScheduledTask(ScheduledTaskDto dto) {
        return postService.actWithScheduledPost(dto);
    }
}
