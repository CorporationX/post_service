package faang.school.postservice.repository;

import faang.school.postservice.model.Resource;
import feign.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ResourceRepository extends JpaRepository<Resource, Long> {

    Resource deleteByKey(String key);

    @Query("""
            SELECT r FROM Resource r
                        WHERE r.id IN :resourceId
            """)
    List<Resource> findAllById(@Param("resourceId") List<Long> resourceId);
}