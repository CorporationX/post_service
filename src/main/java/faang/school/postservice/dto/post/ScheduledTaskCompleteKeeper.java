package faang.school.postservice.dto.post;

import faang.school.postservice.model.scheduled.ScheduledTask;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScheduledTaskCompleteKeeper<T> {
    private List<T> entities;
    private List<ScheduledTask> scheduledTasks;
}
