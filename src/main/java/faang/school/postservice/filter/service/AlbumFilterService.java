package faang.school.postservice.filter.service;

import faang.school.postservice.dto.albums.AlbumFilterDto;
import faang.school.postservice.model.Album;

import java.util.stream.Stream;

public interface AlbumFilterService {
    Stream<Album> applyFilters(Stream<Album> albums, AlbumFilterDto albumFilterDto);
}
