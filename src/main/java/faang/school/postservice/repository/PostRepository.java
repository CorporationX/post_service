package faang.school.postservice.repository;

import faang.school.postservice.model.Post;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface PostRepository extends CrudRepository<Post, Long> {

    List<Post> findByAuthorId(long authorId);

    List<Post> findByProjectId(long projectId);

    List<Post> findPostsByPublishedIsFalseAndAiCheckedIsFalse(Pageable pageable);

    List<Post> findByAuthorIdInAndIdNotIn(List<Long> authorId, List<Long> ids, Pageable pageable);
}
