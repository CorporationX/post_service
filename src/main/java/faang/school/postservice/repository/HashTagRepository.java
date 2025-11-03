package faang.school.postservice.repository;

import faang.school.postservice.model.Comment;
import faang.school.postservice.model.HashTag;
import faang.school.postservice.model.Post;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HashTagRepository extends CrudRepository<HashTag, Long> {
    HashTag getHashTagByName(String name);
}
