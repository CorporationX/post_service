package faang.school.postservice.repository;

import faang.school.postservice.model.Hashtag;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HashtagElasticsearchRepository extends ElasticsearchRepository<Hashtag, Long> {
}