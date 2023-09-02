package faang.school.postservice.filter.album;

import faang.school.postservice.dto.album.AlbumFilterDto;
import faang.school.postservice.model.Album;

import java.time.LocalDate;
import java.util.stream.Stream;

public class AlbumUpdateAtFilter implements AlbumFilter {
    @Override
    public boolean isApplicable(AlbumFilterDto albumFilterDto) {
        return albumFilterDto.getUpdatedAt() != null;
    }

    @Override
    public Stream<Album> apply(Stream<Album> albums, AlbumFilterDto albumFilterDto) {
        LocalDate filterDate = albumFilterDto.getUpdatedAt().toLocalDate();
        return albums.filter(album -> album.getUpdatedAt().toLocalDate().isEqual(filterDate));
    }
}
