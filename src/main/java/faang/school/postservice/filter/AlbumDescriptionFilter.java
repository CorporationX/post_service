package faang.school.postservice.filter;

import faang.school.postservice.dto.album.AlbumFilterDto;
import faang.school.postservice.model.Album;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class AlbumDescriptionFilter implements AlbumFilter {
    @Override
    public boolean isApplicable(AlbumFilterDto albumFilterDto) {
        return albumFilterDto.getDescriptionPattern() != null;
    }

    @Override
    public Stream<Album> apply(AlbumFilterDto albumFilterDto, Stream<Album> albums) {
        return albums.filter(album -> album.getDescription().contains(albumFilterDto.getDescriptionPattern()));
    }
}
