package faang.school.postservice.filter.album;

import faang.school.postservice.dto.albums.AlbumFilterDto;
import faang.school.postservice.model.Album;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.stream.Stream;

public class BeforeDateFilter implements AlbumFilter {
    @Override
    public boolean isApplicable(AlbumFilterDto albumFilterDto) {
        return albumFilterDto.getBeforeDate() != null;
    }

    @Override
    public Stream<Album> apply(Stream<Album> albums, AlbumFilterDto albumFilterDto) {
        LocalDate beforeDate = LocalDate.parse(albumFilterDto.getBeforeDate(), DateTimeFormatter.ISO_DATE);
        return albums.filter(album -> isBeforeOrEqual(album, beforeDate));
    }

    private boolean isBeforeOrEqual(Album album, LocalDate beforeDate) {
        LocalDate albumDate = album.getCreatedAt().toLocalDate();
        return albumDate.isBefore(beforeDate) || albumDate.isEqual(beforeDate);
    }
}
