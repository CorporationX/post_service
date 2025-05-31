package faang.school.postservice.repository.outbox;

import faang.school.postservice.model.outbox.OutboxFeedEvent;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface OutboxRepository extends JpaRepository<OutboxFeedEvent, UUID> {

    @Query("SELECT e FROM OutboxFeedEvent e WHERE e.processed = false ORDER BY e.createdAt ASC")
    List<OutboxFeedEvent> findUnprocessedEvents(Pageable pageable);

    @Modifying
    @Query("DELETE FROM OutboxFeedEvent e WHERE e.processed = true AND e.createdAt < :threshold")
    int deleteOldProcessed(@Param("threshold") LocalDateTime threshold);
}
