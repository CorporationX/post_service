package faang.school.postservice.redisdemo.repository;

import faang.school.postservice.redisdemo.entity.Article;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArticleRepository extends CrudRepository<Article, Long> {
}
