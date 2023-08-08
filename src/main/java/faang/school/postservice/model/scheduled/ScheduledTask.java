package faang.school.postservice.model.scheduled;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "scheduled_tasks")
public class ScheduledTask {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "entity_type")
    @Enumerated(EnumType.STRING)
    private ScheduledEntityType entityType;

    @Column(name = "task_type")
    @Enumerated(EnumType.STRING)
    private ScheduledTaskType taskType;

    @Column(name = "entity_id", unique = true)
    private long entityId;

    @Column(name = "retry_count")
    private int retryCount;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private ScheduledTaskStatus status;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "scheduled_at")
    private LocalDateTime scheduledAt;
}
