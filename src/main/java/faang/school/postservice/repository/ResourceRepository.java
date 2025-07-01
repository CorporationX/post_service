package faang.school.postservice.repository;

import faang.school.postservice.entity.resource.Resource;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResourceRepository extends JpaRepository<Resource, Long> {
}
