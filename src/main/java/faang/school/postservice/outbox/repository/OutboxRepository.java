package faang.school.postservice.outbox.repository;

import faang.school.postservice.outbox.entity.OutboxEvent;
import faang.school.postservice.outbox.entity.OutboxStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OutboxRepository extends JpaRepository<OutboxEvent, Long> {
    Page<OutboxEvent> findByStatusAndSourceService(OutboxStatus status, String sourceService, Pageable pageable);
}
