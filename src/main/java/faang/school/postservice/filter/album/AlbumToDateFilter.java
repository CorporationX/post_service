package faang.school.postservice.filter.album;

import faang.school.postservice.dto.album.AlbumFilterDto;
import faang.school.postservice.model.Album;

import java.util.stream.Stream;

public class AlbumToDateFilter implements AlbumFilter {
    @Override
    public boolean isApplicable(AlbumFilterDto albumFilterDto) {
        return albumFilterDto.getToDate() != null;
    }

    @Override
    public Stream<Album> filter(Stream<Album> albumStream, AlbumFilterDto albumFilterDto) {
        if (isApplicable(albumFilterDto)) {
            return albumStream.filter(album -> album.getUpdatedAt().isBefore(albumFilterDto.getToDate()));
        }
        return albumStream;
    }
}