package faang.school.postservice.repository;

import faang.school.postservice.model.PostAlbum;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostAlbumRepository extends JpaRepository<PostAlbum, Long> {
}
