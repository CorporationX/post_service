package faang.school.postservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserRepository extends JpaRepository<Object, Long> {


    @Query(nativeQuery = true, value = """
            SELECT follower_id
            FROM user_service.subscription
            WHERE followee_id = :authorId
            """)
    List<Long> findFollowerIdsByAuthorId(@Param("authorId") Long authorId);

    @Query(nativeQuery = true, value = """
            SELECT followee_id
            FROM user_service.subscription
            WHERE follower_id = :userId
            """)
    List<Long> findFolloweesIdsByUserId(@Param("userId")Long userId);

}
