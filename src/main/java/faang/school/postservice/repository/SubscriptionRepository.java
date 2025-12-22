package faang.school.postservice.repository;

import faang.school.postservice.model.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

	@Query(nativeQuery = true, value = """
			SELECT s.follower_id
			FROM subscription AS s
			WHERE s.followee_id = :authorId
			""")
	List<Long> findFollowerIdsByFolloweeId(Long authorId);
}