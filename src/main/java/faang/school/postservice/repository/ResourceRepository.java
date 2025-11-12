package faang.school.postservice.repository;

import faang.school.postservice.model.resource.Resource;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ResourceRepository extends CrudRepository<Resource, Long> {
    List<Resource> findByPostId(Long postId);
    long countByPostId(Long postId);
    void deleteByPostId(Long postId);
}
