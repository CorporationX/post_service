package faang.school.postservice.repository;

import faang.school.postservice.model.Subscription;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubscriptionRepository extends CrudRepository<Subscription, Long> {

    @Query(value = """
        select distinct follower_id from subscription
    """, nativeQuery = true)
    List<Long> getAllFollowerIds();
}
