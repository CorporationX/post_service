package faang.school.postservice.repository;

import faang.school.postservice.model.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    List<Long> findFollowerIdByFolloweeId(Long followeeId);
}
