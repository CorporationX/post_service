package faang.school.postservice.repository;

import faang.school.postservice.model.Resource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ResourceRepository extends JpaRepository<Resource, Long> {

    List<Resource> findByPostId(Long postId);

    List<Resource> findByPostIdAndType(Long postId, String type);

    @Query("SELECT COUNT(r) FROM Resource r WHERE r.post.id = :postId AND r.type = :type")
    long countByPostIdAndType(@Param("postId") Long postId, @Param("type") String type);

    Optional<Resource> findByIdAndPostId(Long id, Long postId);

    void deleteByPostId(Long postId);

    @Modifying
    @Query("DELETE FROM Resource r WHERE r.id IN :resourceIds")
    void deleteAllByIdIn(@Param("resourceIds") List<Long> resourceIds);

    List<Resource> findAllByIdIn(List<Long> resourceIds);

    boolean existsByKey(String key);

    Optional<Resource> findByKey(String key);
}