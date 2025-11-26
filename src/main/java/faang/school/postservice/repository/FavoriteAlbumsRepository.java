package faang.school.postservice.repository;

import faang.school.postservice.model.FavoriteAlbums;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FavoriteAlbumsRepository extends JpaRepository<FavoriteAlbums, Long> {

    boolean existsByAlbumIdAndUserId(long albumId, long userId);

    void deleteByAlbumIdAndUserId(long albumId, long userId);
}
