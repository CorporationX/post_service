package faang.school.postservice.repository;

import faang.school.postservice.hash.PostHash;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.LinkedHashSet;
import java.util.List;


@Repository
public interface PostHashRepository extends CrudRepository<PostHash, Long> {
    @Query("SELECT p FROM PostHash p WHERE p.postId IN :postIds")
    List<PostHash> findByIds(LinkedHashSet<Long> postIds);

}
