package faang.school.postservice.util.validator;

import faang.school.postservice.model.scheduled.ScheduledEntityType;
import faang.school.postservice.model.scheduled.ScheduledTask;
import faang.school.postservice.util.exception.EntitySchedulingException;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ScheduledPostServiceValidator {

    public void validateToActWithPostBySchedule(Optional<ScheduledTask> scheduledTask) {

        if (scheduledTask.isPresent() && scheduledTask.get().getEntityType().equals(ScheduledEntityType.POST)) {
            throw new EntitySchedulingException(
                    String.format("Post with id = %d already scheduled", scheduledTask.get().getEntityId()));
        }
    }
}
