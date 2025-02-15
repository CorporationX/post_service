package faang.school.postservice.repository;

import faang.school.postservice.model.Resource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ResourceRepository extends JpaRepository<Resource, Long> {

    @Query("SELECT r.id FROM Resource r WHERE r.key = :key")
    Optional<Long> findIdByKey(String key);
}
