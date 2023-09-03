package faang.school.postservice.repository;

import faang.school.postservice.model.scheduled.ScheduledEntityType;
import faang.school.postservice.model.scheduled.ScheduledTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ScheduledTaskRepository extends JpaRepository<ScheduledTask, Long> {

    @Query("SELECT t FROM ScheduledTask t WHERE t.entityType = :entityType AND t.entityId = :id")
    Optional<ScheduledTask> findScheduledTaskById(Long id, ScheduledEntityType entityType);

    @Query(nativeQuery = true, value = "SELECT t.* FROM scheduled_tasks t " +
            "WHERE t.entity_type = 'POST' " +
            "AND ((t.status = 'NEW' AND t.scheduled_at <= CURRENT_TIMESTAMP) OR t.status = 'ERROR') " +
            "LIMIT :limit")
    List<ScheduledTask> findNewOrErrorPostsToPublishOrDelete(long limit);
}
