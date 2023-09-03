package faang.school.postservice.dto.post;

import com.fasterxml.jackson.annotation.JsonProperty;
import faang.school.postservice.model.scheduled.ScheduledEntityType;
import faang.school.postservice.model.scheduled.ScheduledTaskStatus;
import faang.school.postservice.model.scheduled.ScheduledTaskType;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ScheduledTaskDto(

        @NotNull(message = "Need to set entity type: POST, COMMENT OR LIKE")
        ScheduledEntityType entityType,

        @NotNull(message = "Need to set type of task: PUBLISHING or DELETING")
        ScheduledTaskType taskType,

        @NotNull(message = "Need to set entity id")
        Long entityId,

        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        int retryCount,

        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        ScheduledTaskStatus status,

        @NotNull(message = "Need to set time to publish by schedule")
        LocalDateTime scheduleAt

) {
}
