package faang.school.postservice.repository;

import faang.school.postservice.model.Album;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Repository
public interface AlbumRepository extends CrudRepository<Album, Long> {

    @Query("SELECT a FROM Album a WHERE a.visibility = 'PUBLIC' OR " +
            "(a.visibility = 'SUBSCRIBERS' AND :userId IN (SELECT u.id FROM User u WHERE u.id = :userId)) OR " +
            "(a.visibility = 'SELECTED_USERS' AND :userId IN (SELECT userId FROM a.allowedUsers)) OR " +
            "(a.visibility = 'AUTHOR' AND a.author.id = :userId)")
    List<Album> findVisibleAlbums(@Param("userId") Long userId);

    boolean existsByTitleAndAuthorId(String title, long authorId);

    Stream<Album> findByAuthorId(long authorId);

    @Query("SELECT a FROM Album a LEFT JOIN FETCH a.posts WHERE a.id = :id")
    Optional<Album> findByIdWithPosts(long id);

    @Query(nativeQuery = true, value = "INSERT INTO favorite_albums (album_id, user_id) VALUES (:albumId, :userId)")
    @Modifying
    void addAlbumToFavorites(long albumId, long userId);

    @Query(nativeQuery = true, value = "DELETE FROM favorite_albums WHERE album_id = :albumId AND user_id = :userId")
    @Modifying
    void deleteAlbumFromFavorites(long albumId, long userId);

    @Query(nativeQuery = true, value = """
            SELECT * FROM album
            WHERE id IN (
                SELECT album_id FROM favorite_albums WHERE user_id = :userId
            )
            """)
    Stream<Album> findFavoriteAlbumsByUserId(long userId);
}
