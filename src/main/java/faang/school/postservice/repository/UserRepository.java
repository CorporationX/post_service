package faang.school.postservice.repository;

import faang.school.postservice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {


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

    @Query(nativeQuery = true, value = """
            Select id
            FROM user_service.users
            """)
    List<Long> findAllUsersIds();

}
