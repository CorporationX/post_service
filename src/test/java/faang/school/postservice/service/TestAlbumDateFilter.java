package faang.school.postservice.service;

import faang.school.postservice.filters.AlbumFilter;
import faang.school.postservice.filters.AlbumFilterDto;
import faang.school.postservice.model.Album;

import java.time.LocalDateTime;
import java.util.stream.Stream;

public class TestAlbumDateFilter implements AlbumFilter {

    @Override
    public boolean isApplicable(AlbumFilterDto albumFilterDto) {
        return true;
    }

    @Override
    public Stream<Album> apply(Stream<Album> albums, AlbumFilterDto albumFilterDto) {
        return albums.filter(album -> album
                .getCreatedAt().getYear() == LocalDateTime.now().getYear());
    }
}
