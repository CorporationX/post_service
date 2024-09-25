package faang.school.postservice.repository;

import faang.school.postservice.model.Resource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ResourceRepository extends JpaRepository<Resource, Long> {
//
//
//    @Query(nativeQuery = true, value = """
//            SELECT COUNT(*) FROM post_resources
//            WHERE type = 'image' AND post_id = :postId
//            """)
//     int countImagesInPost(Long postId);
}
