package faang.school.postservice.filter.album;

import faang.school.postservice.dto.AlbumFilterDto;
import faang.school.postservice.model.Album;

import java.util.stream.Stream;

public class AlbumFromDateFilter implements AlbumFilter {
    @Override
    public boolean isApplicable(AlbumFilterDto albumFilterDto) {
        return albumFilterDto.getFromDate() != null;
    }

    @Override
    public Stream<Album> filter(Stream<Album> albumStream, AlbumFilterDto albumFilterDto) {
        if (isApplicable(albumFilterDto)) {
            return albumStream.filter(album -> album.getUpdatedAt().isAfter(albumFilterDto.getFromDate()));
        }
        return albumStream;
    }
}