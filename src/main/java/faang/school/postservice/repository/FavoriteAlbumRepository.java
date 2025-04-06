package faang.school.postservice.repository;

import faang.school.postservice.model.FavoriteAlbum;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FavoriteAlbumRepository extends JpaRepository<FavoriteAlbum, Long> {

}
