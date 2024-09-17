package faang.school.postservice.service.filter;

import faang.school.postservice.dto.album.AlbumFilterDto;
import faang.school.postservice.model.Album;
import faang.school.postservice.repository.AlbumRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class AlbumFilterByTitlePattern implements AlbumFilter {
    private final AlbumRepository albumRepository;

    @Override
    public boolean isApplicable(AlbumFilterDto albumFilterDto) {
        return Objects.nonNull(albumFilterDto.getTitlePattern());
    }

    @Override
    public List<Album> getAlbums(AlbumFilterDto albumFilterDto) {
        String titlePattern = albumFilterDto.getTitlePattern();
        return albumRepository.findByTitleContainingIgnoreCase(titlePattern);
    }
}
