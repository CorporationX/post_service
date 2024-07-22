package faang.school.postservice.filter.album;

import faang.school.postservice.dto.AlbumFilterDto;
import faang.school.postservice.model.Album;

import java.util.stream.Stream;

public class AlbumDescriptionFilter implements AlbumFilter {
    @Override
    public boolean isApplicable(AlbumFilterDto albumFilterDto) {
        return albumFilterDto.getDescriptionPattern() != null;
    }

    @Override
    public Stream<Album> filter(Stream<Album> albumStream, AlbumFilterDto albumFilterDto) {
        if (isApplicable(albumFilterDto)) {
            return albumStream
                    .filter(album -> album.getDescription().toLowerCase()
                            .contains(albumFilterDto.getDescriptionPattern().toLowerCase()));
        }
        return albumStream;
    }
}