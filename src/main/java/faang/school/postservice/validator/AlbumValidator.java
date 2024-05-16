package faang.school.postservice.validator;

import faang.school.postservice.dto.AlbumDto;
import faang.school.postservice.repository.AlbumRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AlbumValidator {

    private final AlbumRepository albumRepository;

    public void validateUniqueTitle(AlbumDto albumDto) {
        boolean isContains = albumRepository.findByAuthorId(albumDto.getAuthorId())
                .anyMatch(album -> album.getTitle().equals(albumDto.getTitle()));
        if (isContains) {
            throw new IllegalArgumentException("Title must be unique");
        }
    }
}
