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
//        if (albumFilterDto != null) {
//            albums = filters.stream()
//                    .filter(filter -> filter.isApplicable(albumFilterDto))
//                    .reduce(albums, (a, filter) -> filter.apply(a, albumFilterDto), (a, b) -> b);
//        }
        if (albumFilterDto != null) {
            System.out.println("Filters available: " + filters.size());
            albums = filters.stream()
                    .filter(filter -> {
                        boolean applicable = filter.isApplicable(albumFilterDto);
//                        System.out.println("Filter " + filter.getClass().getSimpleName() + " is applicable: " + applicable);
                        log.info("Filter {} is applicable: {}", filter.getClass().getSimpleName(), applicable);
                        return applicable;
                    })
                    .reduce(albums, (a, filter) -> {
//                        System.out.println("Applying filter: " + filter.getClass().getSimpleName());
                        log.info("Applying filter: {}", filter.getClass().getSimpleName());
                        return filter.apply(a, albumFilterDto);
                    }, (a, b) -> b);
        }
        return albums;
    }
}
