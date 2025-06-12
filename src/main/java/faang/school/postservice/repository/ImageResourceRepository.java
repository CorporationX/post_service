package faang.school.postservice.repository;

import faang.school.postservice.model.ImageResource;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageResourceRepository extends JpaRepository<ImageResource, Long> {
}
