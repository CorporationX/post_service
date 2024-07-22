package faang.school.postservice.repository;

import faang.school.postservice.model.PostLike;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostLikeRepository extends CrudRepository<PostLike, Long> {

    void deleteByPostIdAndUserId(long postId, long userId);

}
