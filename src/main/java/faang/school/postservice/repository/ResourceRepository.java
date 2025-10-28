package faang.school.postservice.repository;

import faang.school.postservice.model.Resource;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ResourceRepository extends JpaRepository<Resource, Long> {
    List<Resource> findByPostId(Long postId);
}
