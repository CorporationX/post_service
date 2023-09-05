package faang.school.postservice.service.scheduledtaskactor;

import faang.school.postservice.model.Post;
import faang.school.postservice.model.scheduled.ScheduledTask;
import faang.school.postservice.model.scheduled.ScheduledTaskType;
import org.springframework.stereotype.Component;

@Component
public class ScheduledDeletingPostFieldsSetterImpl implements ScheduledPostFieldsSetter {

    @Override
    public boolean isApplicable(ScheduledTask task) {
        return task.getTaskType() == ScheduledTaskType.DELETING_POST;
    }

    @Override
    public void set(ScheduledTask task, Post post) {
        if (isApplicable(task)) {
            post.setPublished(false);
            post.setDeleted(true);
        }
    }
}
