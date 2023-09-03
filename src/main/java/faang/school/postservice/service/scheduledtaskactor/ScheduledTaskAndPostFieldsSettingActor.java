package faang.school.postservice.service.scheduledtaskactor;

import faang.school.postservice.model.Post;
import faang.school.postservice.model.scheduled.ScheduledTask;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ScheduledTaskAndPostFieldsSettingActor {

    private final List<ScheduledPostFieldsSetter> scheduledPostFieldsSetterList;

    public void setFieldsForScheduledPost(Post post, ScheduledTask task) {
        scheduledPostFieldsSetterList.forEach(setter -> setter.set(task, post));
    }
}
