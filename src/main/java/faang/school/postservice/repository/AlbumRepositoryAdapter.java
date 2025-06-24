package faang.school.postservice.repository;

import faang.school.postservice.exception.DataNotFoundException;
import faang.school.postservice.model.Album;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AlbumRepositoryAdapter {
    private final AlbumRepository albumRepository;

    public Album findById(long albumId) {
        return albumRepository.findById(albumId)
                .orElseThrow(() -> new DataNotFoundException(
                        String.format("Альбом с id: %d не найден!", albumId)));
    }
}
