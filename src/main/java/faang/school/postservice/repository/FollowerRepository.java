package faang.school.postservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FollowerRepository extends JpaRepository<Object, Long> {
    /**
     * Получает список ID всех подписчиков конкретного пользователя
     * Адаптировано под вашу схему БД
     *
     * @param userId ID пользователя, для которого нужно найти подписчиков
     * @return список ID подписчиков
     */
    @Query(value = """
        SELECT f.follower_id 
        FROM user_subscription f 
        WHERE f.followee_id = :userId 
        AND f.status = 'ACCEPTED'
        ORDER BY f.created_at DESC
        """, nativeQuery = true)
    List<Long> findFollowerIdsByUserId(@Param("userId") Long userId);

    /**
     * Получает количество подписчиков пользователя
     *
     * @param userId ID пользователя
     * @return количество активных подписчиков
     */
    @Query(value = """
        SELECT COUNT(f.follower_id) 
        FROM user_subscription f 
        WHERE f.followee_id = :userId 
        AND f.status = 'ACCEPTED'
        """, nativeQuery = true)
    Long countActiveFollowersByUserId(@Param("userId") Long userId);

    /**
     * Проверяет, подписан ли один пользователь на другого
     *
     * @param followerId ID подписчика
     * @param followeeId ID того, на кого подписаны
     * @return true если подписка активна
     */
    @Query(value = """
        SELECT EXISTS(
            SELECT 1 FROM user_subscription f 
            WHERE f.follower_id = :followerId 
            AND f.followee_id = :followeeId 
            AND f.status = 'ACCEPTED'
        )
        """, nativeQuery = true)
    boolean isFollowing(@Param("followerId") Long followerId, @Param("followeeId") Long followeeId);

    /**
     * Получает список подписчиков с пагинацией
     *
     * @param userId ID пользователя
     * @param limit количество записей
     * @param offset смещение
     * @return список ID подписчиков
     */
    @Query(value = """
        SELECT f.follower_id 
        FROM user_subscription f 
        WHERE f.followee_id = :userId 
        AND f.status = 'ACCEPTED'
        ORDER BY f.created_at DESC
        LIMIT :limit OFFSET :offset
        """, nativeQuery = true)
    List<Long> findFollowerIdsByUserIdWithPagination(@Param("userId") Long userId,
                                                     @Param("limit") int limit,
                                                     @Param("offset") int offset);
}