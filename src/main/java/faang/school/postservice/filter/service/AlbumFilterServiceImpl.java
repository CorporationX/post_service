package faang.school.postservice.filter.service;

import faang.school.postservice.dto.albums.AlbumFilterDto;
import faang.school.postservice.filter.album.AlbumFilter;
import faang.school.postservice.model.Album;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class AlbumFilterServiceImpl implements AlbumFilterService {
    private final List<AlbumFilter> filters;

    @Override
    public Stream<Album> applyFilters(Stream<Album> albums, AlbumFilterDto albumFilterDto) {
        if (albumFilterDto != null) {
            albums = filters.stream()
                    .filter(filter -> filter.isApplicable(albumFilterDto))
                    .reduce(albums, (a, filter) -> filter.apply(a, albumFilterDto), (a, b) -> b);
        }
        return albums;
    }
}
