package faang.school.postservice.repository.adapter;

import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.model.Album;
import faang.school.postservice.model.FavoriteAlbum;
import faang.school.postservice.repository.FavoriteAlbumRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class FavoriteAlbumRepositoryAdapter {

    private final FavoriteAlbumRepository favoriteAlbumRepository;

    public boolean existsByAlbumAndAuthorId(Album album, Long authorId) {
        return favoriteAlbumRepository.existsByAlbumAndUserId(album, authorId);
    }

    public void save(FavoriteAlbum favoriteAlbum) {
        favoriteAlbumRepository.save(favoriteAlbum);
    }

    public void deleteByAlbumAndUserId(Album album, Long userId) {
        favoriteAlbumRepository.deleteByAlbumAndUserId(album, userId);
    }

    public List<FavoriteAlbum> getByUserId(Long userId) {
        return favoriteAlbumRepository.findByUserId(userId);
    }

    public List<FavoriteAlbum> findAll(Specification<FavoriteAlbum> specification) {
        return favoriteAlbumRepository.findAll(specification);
    }
}