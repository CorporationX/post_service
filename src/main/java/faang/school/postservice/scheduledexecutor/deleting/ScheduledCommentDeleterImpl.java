package faang.school.postservice.scheduledexecutor.deleting;

import faang.school.postservice.dto.post.ScheduledTaskDto;
import faang.school.postservice.scheduledexecutor.ScheduledTaskExecutor;
import faang.school.postservice.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ScheduledCommentDeleterImpl implements ScheduledTaskExecutor {

    private final CommentService commentService;

    @Override
    public void execute(ScheduledTaskDto dto) {
        commentService.addToDeleteCommentBySchedule(dto);
    }
}
