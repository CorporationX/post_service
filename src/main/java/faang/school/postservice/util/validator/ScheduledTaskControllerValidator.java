package faang.school.postservice.util.validator;

import faang.school.postservice.dto.post.ScheduledTaskDto;
import faang.school.postservice.model.scheduled.ScheduledEntityType;
import faang.school.postservice.scheduledexecutor.ScheduledTaskExecutor;
import faang.school.postservice.util.exception.DataValidationException;
import faang.school.postservice.util.exception.InvalidKeyException;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;

@Component
public class ScheduledTaskControllerValidator {

    public void addTaskBySchedule(Map<ScheduledEntityType, ScheduledTaskExecutor> scheduledTaskExecutor,
                                  ScheduledTaskDto dto) {

        if (!scheduledTaskExecutor.containsKey(dto.entityType())) {
            throw new InvalidKeyException(
                    "Entity type is incorrect: " +
                            String.format("entityType: %s", dto.entityType())
            );
        }

        if (dto.scheduleAt().isBefore(LocalDateTime.now())) {
            throw new DataValidationException("Scheduled time must be in future");
        }
    }
}
