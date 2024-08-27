package faang.school.postservice.filter.album;

import faang.school.postservice.dto.album.AlbumFilterDto;
import faang.school.postservice.model.Album;

import java.util.stream.Stream;

public class AlbumAuthorFilter implements AlbumFilter {
    @Override
    public boolean isApplicable(AlbumFilterDto albumFilterDto) {
        return albumFilterDto.getAuthorIdList() != null;
    }

    @Override
    public Stream<Album> filter(Stream<Album> albumStream, AlbumFilterDto albumFilterDto) {
        if (isApplicable(albumFilterDto)) {
            return albumStream.filter(album -> albumFilterDto.getAuthorIdList().contains(album.getAuthorId()));
        }
        return albumStream;
    }
}