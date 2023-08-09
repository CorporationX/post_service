package faang.school.postservice.util.validator;

import faang.school.postservice.dto.post.ScheduledTaskDto;
import faang.school.postservice.scheduledexecutor.ScheduledTaskExecutor;
import faang.school.postservice.util.exception.InvalidKeyException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class ScheduledTaskControllerValidator {

    public void addTaskBySchedule(Map<List<String>, ScheduledTaskExecutor> scheduledTaskExecutor,
                                  ScheduledTaskDto dto) {

        if (!scheduledTaskExecutor.containsKey(List.of(
                dto.entityType().toString(), dto.taskType().toString()))) {
            throw new InvalidKeyException(
                    "Some keys are incorrect: " +
                            String.format("entityType: %s, taskType: %s", dto.entityType(), dto.taskType())
            );
        }
    }
}
