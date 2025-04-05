package faang.school.postservice.repository;

import faang.school.postservice.dto.albums.AlbumDto;
import faang.school.postservice.model.Album;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.stream.Stream;

@Repository
public interface AlbumRepository extends JpaRepository<Album, Long> {

    Album findById(long id);

    boolean existsByTitleAndAuthorId(String title, long authorId);

    @Query(nativeQuery = true, value = "SELECT EXISTS(SELECT 1 FROM favorite_albums WHERE album_id = :albumId AND user_id = :userId)")
    boolean checkAlbumExistsInFavorites(long albumId, long userId);

    boolean existsByIdAndAuthorId(long id, long authorId);

    @Query(nativeQuery = true, value = "INSERT INTO favorite_albums (album_id, user_id) VALUES (:albumId, :userId)")
    @Modifying
    void addAlbumToFavorites(long albumId, long userId);

    //void addAlbumToFavorites(long albumId, long authorId);

    @Query(nativeQuery = true, value = "DELETE FROM favorite_albums WHERE album_id = :albumId AND user_id = :userId")
    @Modifying
    void deleteAlbumFromFavorites(long albumId, long userId);

    Stream<Album> findByAuthorId(long authorId);

    // Stream<Album> findFavoriteAlbumsByUserId(long userId);

//      void update(AlbumDto albumDto, Album albumToUpdate);

}
