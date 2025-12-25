package faang.school.postservice.repository;

import faang.school.postservice.model.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

    @Query(nativeQuery = true, value = """
            SELECT follower_id FROM subscription
            WHERE followee_id = :followeeId
            """)
    List<Long> findFollowerIdsByFolloweeId(long followeeId);


}