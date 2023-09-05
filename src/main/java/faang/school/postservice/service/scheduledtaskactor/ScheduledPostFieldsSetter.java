package faang.school.postservice.service.scheduledtaskactor;

import faang.school.postservice.model.Post;
import faang.school.postservice.model.scheduled.ScheduledTask;

public interface ScheduledPostFieldsSetter {

    boolean isApplicable(ScheduledTask task);

    void set(ScheduledTask task, Post post);
}
