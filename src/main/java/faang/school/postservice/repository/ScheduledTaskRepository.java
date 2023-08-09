package faang.school.postservice.repository;

import faang.school.postservice.model.scheduled.ScheduledTask;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScheduledTaskRepository extends JpaRepository<ScheduledTask, Long> {
}
