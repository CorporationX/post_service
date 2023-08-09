package faang.school.postservice.repository;

import faang.school.postservice.model.scheduled.ScheduledTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ScheduledTaskRepository extends JpaRepository<ScheduledTask, Long> {

    @Query("SELECT t FROM ScheduledTask t WHERE t.entityType = 'POST' AND t.entityId = :id")
    Optional<ScheduledTask> findPostById(Long id);
}
