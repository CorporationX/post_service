package faang.school.postservice.filter.album;

import faang.school.postservice.dto.album.AlbumFilterDto;
import faang.school.postservice.dto.album.AlbumUpdateDto;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class AlbumDescriptionFilter implements AlbumFilter {
    @Override
    public boolean isApplicable(AlbumFilterDto filter) {
        return filter.getDescriptionPattern() != null && !filter.getDescriptionPattern().isBlank();

    }

    @Override
    public Stream<AlbumUpdateDto> apply(Stream<AlbumUpdateDto> albums, AlbumFilterDto filter) {
        return albums.filter(album -> album.getDescription().equalsIgnoreCase(filter.getDescriptionPattern()));
    }
}
