package faang.school.postservice.repository;

import faang.school.postservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query(nativeQuery = true, value = """
                    SELECT s.follower_id
                    FROM subscription s
                    WHERE s.followee_id = ?1
            """)
    List<Long> findAllSubscribersById(Long id);

    @Query(nativeQuery = true, value = """
                    SELECT s
                    FROM subscription s
                    WHERE s.follower_id = ?1
            """)
    List<User> findByFollowerId(Long id);
}