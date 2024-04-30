package faang.school.postservice.repository;

import faang.school.postservice.model.PostView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Alexander Bulgakov
 */

@Repository
public interface PostViewRepository extends JpaRepository<PostView, Long> {
}
