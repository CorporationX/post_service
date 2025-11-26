package faang.school.postservice.filters;

import faang.school.postservice.model.Album;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class AlbumDateFilter implements AlbumFilter {

    @Override
    public boolean isApplicable(AlbumFilterDto albumFilterDto) {

        return albumFilterDto.fromDate() != null;
    }

    @Override
    public Stream<Album> apply(Stream<Album> albums, AlbumFilterDto albumFilterDto) {

        return albums.filter(album -> isSameDate(album, albumFilterDto));
    }

    private boolean isSameDate(Album album, AlbumFilterDto albumFilterDto) {

        return (album.getCreatedAt().getYear() == albumFilterDto.fromDate().getYear() &&
                album.getCreatedAt().getMonth() == albumFilterDto.fromDate().getMonth() &&
                album.getCreatedAt().getDayOfMonth() == albumFilterDto.fromDate().getDayOfMonth());

    }
}
