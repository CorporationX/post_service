package faang.school.postservice.service.scheduledtaskactor;

import faang.school.postservice.model.Post;
import faang.school.postservice.model.scheduled.ScheduledTask;
import faang.school.postservice.model.scheduled.ScheduledTaskStatus;
import faang.school.postservice.model.scheduled.ScheduledTaskType;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ScheduledTaskAndPostCheckerActor {

    public void actWithCorrespondence(Post post, ScheduledTask task, int limit) {
        List<Boolean> checkList = List.of(!post.isPublished() && task.getTaskType().equals(ScheduledTaskType.PUBLISHING_POST),
                !post.isDeleted() && task.getTaskType().equals(ScheduledTaskType.DELETING_POST));

        checkList.forEach(b -> {
            if (b.equals(true)) {
                if (task.getRetryCount() >= limit) { // если количество ретраев превысило лимит, то статус "FAILURE"
                    task.setRetryCount(task.getRetryCount() + 1);
                    task.setStatus(ScheduledTaskStatus.FAILURE);
                } else {
                    task.setRetryCount(task.getRetryCount() + 1);
                    task.setStatus(ScheduledTaskStatus.ERROR);
                }
            } else {
                task.setRetryCount(task.getRetryCount() + 1); // если пост нормально обновился в бд, то ставлю статус "SUCCESS"
                task.setStatus(ScheduledTaskStatus.SUCCESS);
            }
        });
    }
}
