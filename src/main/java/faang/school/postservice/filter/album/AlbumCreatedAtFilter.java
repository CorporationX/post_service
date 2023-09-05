package faang.school.postservice.filter.album;

import faang.school.postservice.dto.album.AlbumFilterDto;
import faang.school.postservice.model.Album;

import java.time.LocalDate;
import java.util.stream.Stream;

public class AlbumCreatedAtFilter implements AlbumFilter {
    @Override
    public boolean isApplicable(AlbumFilterDto albumFilterDto) {
        return albumFilterDto.getCreatedAt() != null;
    }

    @Override
    public Stream<Album> apply(Stream<Album> albums, AlbumFilterDto albumFilterDto) {
        LocalDate filterDate = albumFilterDto.getCreatedAt().toLocalDate();
        return albums.filter(album -> album.getCreatedAt().toLocalDate().isEqual(filterDate));
    }
}
