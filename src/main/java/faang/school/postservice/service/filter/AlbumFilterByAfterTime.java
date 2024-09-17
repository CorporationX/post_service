package faang.school.postservice.service.filter;

import faang.school.postservice.dto.album.AlbumFilterDto;
import faang.school.postservice.model.Album;
import faang.school.postservice.repository.AlbumRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class AlbumFilterByAfterTime implements AlbumFilter {
    private final AlbumRepository albumRepository;

    @Override
    public boolean isApplicable(AlbumFilterDto albumFilterDto) {
        return Objects.nonNull(albumFilterDto.getAfterThisTime());
    }

    @Override
    public List<Album> getAlbums(AlbumFilterDto albumFilterDto) {
        LocalDateTime after = albumFilterDto.getAfterThisTime();
        return albumRepository.findByCreatedAtAfter(after);
    }
}
