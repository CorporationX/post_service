package faang.school.postservice.repository;

import faang.school.postservice.model.UserAlbumAccess;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserAlbumAccessRepository extends CrudRepository<UserAlbumAccess, Long> {

    @Query(nativeQuery = true, value = """
            SELECT album_id FROM user_album_access
            WHERE user_id = :userId
            """)
    List<Long> findAlbumIdsAllowedUser(Long userId);

    @Query(nativeQuery = true, value = """
            SELECT EXISTS (
                SELECT album_id FROM user_album_access
                WHERE album_id = :albumId AND user_id = :userId
            )
            AS result
            """)
    boolean hasUserAccessToAlbum(Long userId, Long albumId);
}
