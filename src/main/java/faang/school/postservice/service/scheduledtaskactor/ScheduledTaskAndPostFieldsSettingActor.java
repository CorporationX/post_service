package faang.school.postservice.service.scheduledtaskactor;

import faang.school.postservice.model.Post;
import faang.school.postservice.model.scheduled.ScheduledTask;
import faang.school.postservice.model.scheduled.ScheduledTaskType;
import org.springframework.stereotype.Component;

@Component
public class ScheduledTaskAndPostFieldsSettingActor {

    public void setFieldsForScheduledPost(Post post, ScheduledTask task) {
        if (task.getTaskType().equals(ScheduledTaskType.DELETING)) {
            post.setPublished(false);
            post.setDeleted(true);
            post.setUpdatedAt(task.getScheduleAt());
        } else if (task.getTaskType().equals(ScheduledTaskType.PUBLISHING)) {
            post.setPublished(true);
            post.setPublishedAt(task.getScheduleAt());
            post.setUpdatedAt(task.getScheduleAt());
        }
    }
}
