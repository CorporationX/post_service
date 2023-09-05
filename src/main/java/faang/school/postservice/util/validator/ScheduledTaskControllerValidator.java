package faang.school.postservice.util.validator;

import faang.school.postservice.dto.post.ScheduledTaskDto;
import faang.school.postservice.model.scheduled.ScheduledEntityType;
import faang.school.postservice.scheduledexecutor.ScheduledTaskExecutor;
import faang.school.postservice.util.exception.InvalidKeyException;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ScheduledTaskControllerValidator {

    public void validateToAddScheduledTask(Map<ScheduledEntityType, ScheduledTaskExecutor> scheduledTaskExecutor,
                                           ScheduledTaskDto dto) {

        if (!scheduledTaskExecutor.containsKey(dto.entityType())) {
            throw new InvalidKeyException(
                    String.format("Entity type not found: %s", dto.entityType())
            );
        }
    }
}
