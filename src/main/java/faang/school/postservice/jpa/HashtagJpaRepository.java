package faang.school.postservice.jpa;

import faang.school.postservice.model.Hashtag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface HashtagJpaRepository extends JpaRepository<Hashtag, Long> {

    Optional<Hashtag> findByName(String name);
}
