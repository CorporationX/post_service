package faang.school.postservice.filter.album;

import faang.school.postservice.dto.album.AlbumFilterDto;
import faang.school.postservice.dto.album.AlbumUpdateDto;

import java.util.stream.Stream;

public interface AlbumFilter {
    boolean isApplicable(AlbumFilterDto filter);

    Stream<AlbumUpdateDto> apply(Stream<AlbumUpdateDto> albums, AlbumFilterDto filter);
}

