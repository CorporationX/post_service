package faang.school.postservice.repository;

import faang.school.postservice.model.outbox.OutboxEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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
}
