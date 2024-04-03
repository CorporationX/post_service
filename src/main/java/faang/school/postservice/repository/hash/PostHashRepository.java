package faang.school.postservice.repository.hash;

import faang.school.postservice.dto.hash.PostHash;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostHashRepository extends CrudRepository<PostHash, Long> {
}