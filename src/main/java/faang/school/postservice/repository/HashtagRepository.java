package faang.school.postservice.repository;

import faang.school.postservice.model.Hashtag;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

/**
 * Репозитории для взаимодействия с хэштегами в бд
 *
 * @author Myrza
 * @since 08.08.2025
 */
public interface HashtagRepository extends CrudRepository<Hashtag, Long> {
    Optional<Hashtag> findByName(String name);
    List<Hashtag> findAllByNameIn(List<String> names);
}

