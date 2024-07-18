package faang.school.postservice.filter;

import faang.school.postservice.dto.AlbumFilterDto;
import faang.school.postservice.model.Album;

import java.util.stream.Stream;

public class AlbumTitleFilter implements AlbumFilter {
    @Override
    public boolean isApplicable(AlbumFilterDto albumFilterDto) {
        return albumFilterDto.getTitlePattern() != null;
    }

    @Override
    public Stream<Album> filter(Stream<Album> albumStream, AlbumFilterDto albumFilterDto) {
        if (isApplicable(albumFilterDto)) {
            return albumStream.filter(album -> album.getTitle().toLowerCase()
                    .contains(albumFilterDto.getTitlePattern().toLowerCase()));
        }
        return albumStream;
    }
}