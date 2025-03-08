package faang.school.postservice.repository;

import faang.school.postservice.model.Album;
import faang.school.postservice.model.FavoriteAlbum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface FavoriteAlbumRepository extends JpaRepository<FavoriteAlbum, Long>, JpaSpecificationExecutor<FavoriteAlbum> {
    boolean existsByAlbumAndUserId(Album album, Long userId);

    Optional<FavoriteAlbum> findByAlbumAndUserId(Album album, Long userId);

    void deleteByAlbumAndUserId(Album album, Long userId);

    List<FavoriteAlbum> findByUserId(Long userId);
}