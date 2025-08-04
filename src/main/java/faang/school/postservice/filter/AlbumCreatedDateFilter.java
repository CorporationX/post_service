package faang.school.postservice.filter;

import faang.school.postservice.dto.album.AlbumFilterDto;
import faang.school.postservice.model.Album;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class AlbumCreatedDateFilter implements AlbumFilter {

    public boolean isApplicable(AlbumFilterDto albumFilterDto) {
        return albumFilterDto.getCreatedBeforeDate() != null
                || albumFilterDto.getCreatedAfterDate() != null;
    }

    public Stream<Album> apply(Stream<Album> albums, AlbumFilterDto albumFilterDto) {
        if (albumFilterDto.getCreatedBeforeDate() == null) { //после даты
            return albums.filter(
                    album -> !album.getCreatedAt().isBefore(albumFilterDto.getCreatedAfterDate())
            );
        }
        if (albumFilterDto.getCreatedAfterDate() == null) { // до даты
            return albums.filter(
                    album -> !album.getCreatedAt().isAfter(albumFilterDto.getCreatedBeforeDate())
            );
        }
        return albums
                .filter(album -> !album.getCreatedAt().isBefore(albumFilterDto.getCreatedAfterDate()))
                .filter(album -> !album.getCreatedAt().isAfter(albumFilterDto.getCreatedBeforeDate()));
    }
}