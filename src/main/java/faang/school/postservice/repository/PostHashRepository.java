package faang.school.postservice.repository;

import faang.school.postservice.hash.PostHash;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface PostHashRepository extends CrudRepository<PostHash, Long> {

}
