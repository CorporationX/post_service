package faang.school.postservice.repository.outbox;

import faang.school.postservice.model.outbox.EventStatus;
import faang.school.postservice.model.outbox.EventType;
import faang.school.postservice.model.outbox.OutboxEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OutboxEventRepository extends JpaRepository<OutboxEvent, Long> {

    List<OutboxEvent> findTop100ByStatusAndTypeOrderByCreatedAtAsc(EventStatus eventStatus, EventType type);
}
