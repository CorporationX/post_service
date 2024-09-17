package faang.school.postservice.repository.hashtag;

import faang.school.postservice.model.Hashtag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HashtagRepository extends JpaRepository<Hashtag, Long> {

   Hashtag findByName(String name);
}
