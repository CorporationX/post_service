package faang.school.postservice.redisdemo.repository;

import faang.school.postservice.redisdemo.entity.Article;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArticleRepository extends CrudRepository<Article, Long> {

//    List<Article> findByHashTags(List<String> hashTags);

    //    @Query(value = "SELECT * FROM article WHERE hash_tags @> :hash_tags::jsonb", nativeQuery = true)
//    List<Article> findByHashTags(@Param("hash_tags") List<String> hashTags);
//  @Query(value = "SELECT * FROM article WHERE hash_tags @> :hash_tags", nativeQuery = true)
    @Query(value = "SELECT * FROM article WHERE hash_tags @> CAST(:hash_tags AS jsonb)", nativeQuery = true)
    List<Article> findByHashTags(@Param("hash_tags") String hashTags);
}
