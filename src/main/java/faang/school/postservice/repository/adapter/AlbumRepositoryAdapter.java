package faang.school.postservice.repository.adapter;

import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.model.Album;
import faang.school.postservice.repository.AlbumRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AlbumRepositoryAdapter {
    private final AlbumRepository albumRepository;

    public boolean existsByTitleAndAuthorId(String title, Long authorId) {
        return albumRepository.existsByTitleAndAuthorId(title, authorId);
    }

    public Album save(Album album) {
        return albumRepository.save(album);
    }

    public Album getById(Long id) {
        return albumRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Album not found with id: " + id));
    }

    public void delete(Album album) {
        albumRepository.delete(album);
    }

    public List<Album> findAll(Specification<Album> specification) {
        return albumRepository.findAll(specification);
    }
}
