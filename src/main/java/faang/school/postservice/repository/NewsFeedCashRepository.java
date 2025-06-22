package faang.school.postservice.repository;

import faang.school.postservice.cash.NewsFeed;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;

@Component
public interface NewsFeedCashRepository extends CrudRepository<NewsFeed, Long> {
}
