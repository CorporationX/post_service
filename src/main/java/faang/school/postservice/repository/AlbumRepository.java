package faang.school.postservice.repository;

import faang.school.postservice.mapper.AlbumMapper;
import faang.school.postservice.model.Album;
import faang.school.postservice.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface AlbumRepository extends JpaRepository<Album, Long> {
    //Optional<Album> findById(long id);
    Album findById(long id);
//    Optional<Album> findByAuthorId(long authorId);
//    boolean existsByTitleAndAuthorId(String title, long authorId);
//    Optional<Album> findByIdWithPosts(long id);
//    void addAlbumToFavorites(long albumId, long userId);
//    void deleteAlbumFromFavorites(long albumId, long userId);
//    Optional<Album> findFavoriteAlbumsByUserId(long userId);
}
