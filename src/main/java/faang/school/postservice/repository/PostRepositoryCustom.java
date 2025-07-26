package faang.school.postservice.repository;

import faang.school.postservice.model.Post;
import faang.school.postservice.repository.criteria.PostSearchCriteria;

import java.util.List;

public interface PostRepositoryCustom {
    List<Post> findByCriteria(PostSearchCriteria criteria);
}
