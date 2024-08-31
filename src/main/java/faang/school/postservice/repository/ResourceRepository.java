package faang.school.postservice.repository;

import faang.school.postservice.model.Resource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Repository
public interface ResourceRepository extends JpaRepository<Resource, Long> {
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM resource WHERE id = :id RETURNING *", nativeQuery = true)
    Resource popById(@Param("id") Long id);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM resource WHERE id IN :ids RETURNING *", nativeQuery = true)
    List<Resource> popAllByIds(@Param("ids") Set<Long> ids);

    @Transactional
    @Query(value = "SELECT * FROM resource WHERE post_id = :postId", nativeQuery = true)
    List<Resource> findAllByPostId(long postId);

    @Transactional
    @Query(value = "SELECT id from resource WHERE post_id = :postId", nativeQuery = true)
    Set<Long> findAllIdsByPostId(long postId);
}
