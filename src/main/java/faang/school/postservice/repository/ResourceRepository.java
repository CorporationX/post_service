package faang.school.postservice.repository;

import faang.school.postservice.model.entity.Resource;
import faang.school.postservice.model.enums.ResourceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ResourceRepository extends JpaRepository<Resource, Long> {
    List<Resource> findAllByStatusAndUpdatedAtBefore(ResourceStatus status, LocalDateTime date);
}
