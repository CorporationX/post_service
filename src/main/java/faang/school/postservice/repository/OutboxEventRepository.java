package faang.school.postservice.repository;

import faang.school.postservice.model.outbox.OutboxEvent;
import faang.school.postservice.model.outbox.OutboxStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.List;

public interface OutboxEventRepository extends JpaRepository<OutboxEvent, Long> {

    @Query(value = """
            select *
            from outbox_event
            where status = 'NEW'
            order by created_at
            for update skip locked
            limit :limit
            """, nativeQuery = true)
    List<OutboxEvent> pickBatchForUpdateSkipLocked(@Param("limit") int limit);

    @Modifying
    @Query("update OutboxEvent e set e.status = :status, e.updatedAt = :now, e.sentAt = :sentAt where e.id = :id")
    int updateStatus(@Param("id") Long id,
                     @Param("status") OutboxStatus status,
                     @Param("now") OffsetDateTime now,
                     @Param("sentAt") OffsetDateTime sentAt);

    @Modifying
    @Query("update OutboxEvent e set e.status = :status, e.attempts = e.attempts + 1, e.lastError = :err, e.updatedAt = :now where e.id = :id")
    int markFailed(@Param("id") Long id,
                   @Param("status") OutboxStatus status,
                   @Param("err") String err,
                   @Param("now") OffsetDateTime now);
}
