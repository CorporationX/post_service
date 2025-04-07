package faang.school.postservice.repository;

import faang.school.postservice.model.Resource;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ResourceRepository extends CrudRepository<Resource, Long> {

    @Query("SELECT r.key FROM Resource r WHERE r.id = :id")
    Optional<String> findResourceKeyById(Long id);

    @Query("SELECT r.type FROM Resource r WHERE r.id = :id")
    Optional<String> findResourceTypeById(Long id);

    @Modifying
    @Query("DELETE FROM Resource r WHERE r.id = :id")
    void deleteResourceById(Long id);

}
