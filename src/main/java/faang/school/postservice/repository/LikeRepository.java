package faang.school.postservice.repository;

import faang.school.postservice.model.Like;
import org.springframework.data.jpa.repository.JpaRepository;


public interface LikeRepository extends JpaRepository<Like, Long> {

}
