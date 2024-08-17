package faang.school.postservice.filter;

import faang.school.postservice.dto.AlbumFilterDto;
import faang.school.postservice.model.Album;

import java.util.stream.Stream;

public interface AlbumFilter {

    boolean isApplicable(AlbumFilterDto albumFilterDto);

    Stream<Album> filter(Stream<Album> albumStream, AlbumFilterDto albumFilterDto);
}