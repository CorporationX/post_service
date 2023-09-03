package faang.school.postservice.scheduledexecutor;

import faang.school.postservice.dto.post.ScheduledTaskDto;
import faang.school.postservice.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ScheduledCommentExecutorImpl implements ScheduledTaskExecutor {

    private final CommentService commentService;

    @Override
    public ScheduledTaskDto actWithScheduledTask(ScheduledTaskDto dto) {
        return null;
    }
}
