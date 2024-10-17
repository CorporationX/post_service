package faang.school.postservice.repository.hashtag;

import faang.school.postservice.model.entity.Hashtag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HashtagRepository extends JpaRepository<Hashtag, Long> {

   Optional<Hashtag> findByName(String name);
}
