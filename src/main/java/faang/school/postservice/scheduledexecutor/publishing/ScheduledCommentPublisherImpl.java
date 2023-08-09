package faang.school.postservice.scheduledexecutor.publishing;

import faang.school.postservice.dto.post.ScheduledTaskDto;
import faang.school.postservice.scheduledexecutor.ScheduledTaskExecutor;
import faang.school.postservice.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ScheduledCommentPublisherImpl implements ScheduledTaskExecutor {

    private final CommentService commentService;

    @Override
    public void execute(ScheduledTaskDto dto) {
        commentService.actWithCommentBySchedule(dto);
    }
}
